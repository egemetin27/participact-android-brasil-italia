/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.udesc.esag.participactbrasil.services;


/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService {// extends GCMBaseIntentService {

//	private static final String TAG = GCMIntentService.class.getSimpleName();
//	
//	private SpiceManager _contentManager = new SpiceManager( JacksonSpringAndroidSpiceService.class );
//	
//	private String _lastRequestCacheKey;
//
//	
//	public GCMIntentService() {
//		super(ParticipActConfiguration.GCM_SENDER_ID);
//	}
//
//	@Override
//	protected void onRegistered(Context context, String registrationId) {
//		Log.i(TAG, "Device registered: regId = " + registrationId);
//		UserAccountPreferences accountDao = UserAccountPreferences.getInstance(context);
//		UserAccount account = accountDao.getUserAccount();
//		account.setRegistrationId(registrationId);
//		accountDao.saveUserAccount(account);
//		
//		if(!GCMRegistrar.isRegisteredOnServer(context)){
//			GCMRegisterRequest request = new GCMRegisterRequest(context, account.getRegistrationId());
//			_lastRequestCacheKey = request.createCacheKey();
//			if(!_contentManager.isStarted()){
//				_contentManager.start(context);
//			}
//			_contentManager.execute(request, _lastRequestCacheKey, DurationInMillis.ALWAYS_EXPIRED, new GCMRegisterRequestListener());
//		}
//	}
//
//	@Override
//	protected void onUnregistered(Context context, String registrationId) {
//		Log.i(TAG, "Device unregistered");
//		if (GCMRegistrar.isRegisteredOnServer(context)) {
//			UserAccount account = UserAccountPreferences.getInstance(context).getUserAccount();
//			GCMUnregisterRequest request = new GCMUnregisterRequest(context);
//			_lastRequestCacheKey = request.createCacheKey();
//			if(!_contentManager.isStarted()){
//				_contentManager.start(context);
//			}
//			_contentManager.execute(request, _lastRequestCacheKey, DurationInMillis.ALWAYS_EXPIRED, new GCMUnregisterRequestListener());
//		}
//	}
//
//	@Override
//	protected void onMessage(Context context, Intent intent) {
//		Log.i(TAG, "Received message");
//		String message = getString(R.string.gcm_new_message);
//		// notifies user
//		generateNotification(context, message);
//	}
//
//	@Override
//	protected void onDeletedMessages(Context context, int total) {
//		Log.i(TAG, "Received deleted messages notification");
//	}
//
//	@Override
//	public void onError(Context context, String errorId) {
//		Log.i(TAG, "Received error: " + errorId);
//	}
//
//	@Override
//	protected boolean onRecoverableError(Context context, String errorId) {
//		// log message
//		Log.i(TAG, "Received recoverable error: " + errorId);
//		return super.onRecoverableError(context, errorId);
//	}
//
//	/**
//	 * Issues a notification to inform the user that server has sent a message.
//	 */
//	private static void generateNotification(Context context, String message) {
//		int icon = R.drawable.ic_gcm_new_task;
//		NotificationManager notificationManager = (NotificationManager) context
//				.getSystemService(Context.NOTIFICATION_SERVICE);
//		String title = context.getString(R.string.app_name);
//		PendingIntent intent = PendingIntent.getActivity(context, 0, new Intent(), 0);
////		Notification notification = new Notification(icon, message, when);
////		notification.setLatestEventInfo(context, title, message, intent);
//		
//		/*NotificationCompat.Builder creates a Notification obj compatible by Android API 4*/
//		Notification notification = new NotificationCompat.Builder(context)
//										.setContentTitle(title)
//										.setContentText(message)
//										.setSmallIcon(icon)
//										.setWhen(System.currentTimeMillis())
//										.setAutoCancel(true)
//										.setContentIntent(intent)
//										.getNotification();
//		notification.flags |= Notification.FLAG_AUTO_CANCEL;
//		notificationManager.notify(0, notification);
//		Vibrator v = (Vibrator) context
//				.getSystemService(Context.VIBRATOR_SERVICE);
//		v.vibrate(1000);
//	}
//	
//	
//	private class GCMRegisterRequestListener implements RequestListener< Boolean > {
//        @Override
//        public void onRequestFailure( SpiceException e ) {
//        }
//
//        @Override
//        public void onRequestSuccess( Boolean result ) {
//            if ( result == null ) {
//                return;
//            }
//            if(result){
//            	GCMRegistrar.setRegisteredOnServer(getApplicationContext(), true);
//            	Toast.makeText(getApplicationContext(), "registration on server ok", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//	
//	private class GCMUnregisterRequestListener implements RequestListener< Boolean > {
//        @Override
//        public void onRequestFailure( SpiceException e ) {
//        }
//
//        @Override
//        public void onRequestSuccess( Boolean result ) {
//            if ( result == null ) {
//                return;
//            }
//            if(result){
//            	UserAccountPreferences accountDao = UserAccountPreferences.getInstance(getApplicationContext());
//        		UserAccount account = accountDao.getUserAccount();
//        		account.setRegistrationId("");
//        		accountDao.saveUserAccount(account);
//            	GCMRegistrar.setRegisteredOnServer(getApplicationContext(), false);
//            }
//        }
//    }

}
