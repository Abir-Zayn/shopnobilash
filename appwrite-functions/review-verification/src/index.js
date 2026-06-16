import { Client, TablesDB, Users, ID, Permission, Role } from 'node-appwrite';

const ENDPOINT = 'https://sgp.cloud.appwrite.io/v1';
const PROJECT_ID = '6a2bbc29001d7e1307a8';
const DATABASE_ID = 'property_db';
const VERIFICATIONS_TABLE_ID = 'verifications';
const NOTIFICATIONS_TABLE_ID = 'notifications';

export default async ({ req, res, log, error }) => {
  const apiKey = process.env.APPWRITE_API_KEY;
  if (!apiKey) {
    return res.json({ error: 'APPWRITE_API_KEY missing' }, 500);
  }

  const client = new Client()
    .setEndpoint(ENDPOINT)
    .setProject(PROJECT_ID)
    .setKey(apiKey);

  const tablesDB = new TablesDB(client);
  const users = new Users(client);

  const callerId = req.headers['x-appwrite-user-id'];
  if (!callerId) {
    return res.json({ error: 'Unauthorized' }, 401);
  }

  try {
    const caller = await users.get(callerId);
    if (!caller.labels?.includes('admin')) {
      return res.json({ error: 'Forbidden — admin label required' }, 403);
    }
  } catch (e) {
    return res.json({ error: 'Failed to verify caller', detail: e.message, callerId }, 403);
  }

  let body;
  try {
    body = typeof req.body === 'string' ? JSON.parse(req.body) : req.body;
  } catch (e) {
    return res.json({ error: 'Invalid JSON body', detail: e.message }, 400);
  }

  const { verificationId, action, rejectionReason } = body ?? {};

  if (!verificationId || !['approve', 'reject'].includes(action)) {
    return res.json({ error: 'Required: verificationId, action (approve|reject)' }, 400);
  }

  if (action === 'reject' && !rejectionReason) {
    return res.json({ error: 'rejectionReason required when rejecting' }, 400);
  }

  let verification;
  try {
    verification = await tablesDB.getRow(DATABASE_ID, VERIFICATIONS_TABLE_ID, verificationId);
  } catch (e) {
    return res.json({ error: 'Verification not found', detail: e.message }, 404);
  }

  if (verification.verificationStatus !== 'pending') {
    return res.json({ error: 'Already reviewed', status: verification.verificationStatus }, 409);
  }

  const newStatus = action === 'approve' ? 'approved' : 'rejected';

  const updateData = {
    verificationStatus: newStatus,
    reviewedAt: new Date().toISOString(),
    reviewedBy: callerId,
  };
  if (action === 'reject') updateData.rejectReason = rejectionReason;

  try {
    await tablesDB.updateRow(
      DATABASE_ID,
      VERIFICATIONS_TABLE_ID,
      verificationId,
      updateData,
    );
  } catch (e) {
    return res.json({ error: 'Failed to update verification', detail: e.message }, 500);
  }

  const notifTitle = action === 'approve' ? 'Identity Verified' : 'Verification Rejected';
  const notifBody = action === 'approve'
    ? 'Your identity has been successfully verified.'
    : `Your verification was rejected. Reason: ${rejectionReason ?? ''}`;

  try {
    await tablesDB.createRow(
      DATABASE_ID,
      NOTIFICATIONS_TABLE_ID,
      ID.unique(),
      {
        user_id: verification.userId,
        notification_type: 'System',
        title: notifTitle,
        message: notifBody,
        action_target_id: verificationId,
        action_route: 'verify_identity',
        is_read: false,
      },
      [
        Permission.read(Role.user(verification.userId)),
        Permission.update(Role.user(verification.userId)),
      ],
    );
  } catch (e) {
    error('Failed to create notification: ' + e.message);
  }

  return res.json({ success: true, verificationId, status: newStatus });
};
