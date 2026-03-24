import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import javax.swing.*;
import java.awt.*;

public class VideoPlayer extends JPanel {
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private volatile String currentVideoPath = null;
	private volatile boolean downloadComplete = false;
	private volatile long lastTimeMs = 0;
	private final Object mediaPlayerLock = new Object();

	public VideoPlayer() {
		setLayout(new BorderLayout());
		try {
			mediaPlayerComponent = new EmbeddedMediaPlayerComponent() {
				@Override
				protected String[] onGetMediaPlayerFactoryArgs() {
					return new String[] {
						"--audio-resampler=soxr",
						"--audio-time-stretch"
					};
				}
			};
			add(mediaPlayerComponent, BorderLayout.CENTER);

			mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
				@Override
				public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
					if (!downloadComplete && currentVideoPath != null) {
						lastTimeMs = newTime;
					}
				}
			});
		} catch (Exception e) {
			System.err.println("Error initializing video player: " + e.getMessage());
			System.err.println("Make sure VLC is installed: sudo apt-get install vlc");
			e.printStackTrace();
		}
	}

	public void playStreamingVideo(String videoPath) {
		synchronized (mediaPlayerLock) {
			if (mediaPlayerComponent != null && mediaPlayerComponent.getMediaPlayer() != null) {
				MediaPlayer mp = mediaPlayerComponent.getMediaPlayer();

				try {
					if (mp.isPlaying()) {
						mp.stop();
						Thread.sleep(100);
					}

					lastTimeMs = 0;
					currentVideoPath = videoPath;
					downloadComplete = false;
					String[] options = {
						":file-caching=3000",
						":network-caching=3000"
					};
					mp.playMedia(videoPath, options);
				} catch (Exception e) {
					System.err.println("Error playing video: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	public void onNewDataAvailable() {
		if (currentVideoPath == null || downloadComplete)
			return;

		synchronized (mediaPlayerLock) {
			MediaPlayer mp = mediaPlayerComponent.getMediaPlayer();
			if (mp != null && !mp.isPlaying() && lastTimeMs > 0) {
				try {
					long startTime = Math.max(0, (lastTimeMs / 1000) - 1);
					String[] options = {
						":file-caching=3000",
						":network-caching=3000",
						":start-time=" + startTime
					};
					mp.playMedia(currentVideoPath, options);
				} catch (Exception e) {
					System.err.println("Error resuming video: " + e.getMessage());
				}
			}
		}
	}

	public void notifyDownloadComplete() {
		downloadComplete = true;
	}

	public void release() {
		synchronized (mediaPlayerLock) {
			if (mediaPlayerComponent != null) {
				try {
					MediaPlayer mp = mediaPlayerComponent.getMediaPlayer();
					if (mp != null && mp.isPlaying()) {
						mp.stop();
						Thread.sleep(100);
					}
					mediaPlayerComponent.release();
				} catch (Exception e) {
					System.err.println("Error releasing video player: " + e.getMessage());
				}
			}
		}
	}
}
