package com.amoled.clockmusic.media

import android.service.notification.NotificationListenerService

/**
 * Required by Android's MediaSessionManager.getActiveSessions() so Dot can
 * display and control whichever music app the user already has playing
 * (Spotify, YouTube Music, Apple Music, Samsung Music, etc.).
 */
class DotNotificationListenerService : NotificationListenerService()
