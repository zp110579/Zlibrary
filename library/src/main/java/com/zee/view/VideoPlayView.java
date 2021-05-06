package com.zee.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import com.zee.listener.VideoPlayViewListener;

public class VideoPlayView extends TextureView {
    private MediaPlayer mMediaPlayer;
    private Surface surface;
    private VideoPlayViewListener mVideoPlayViewListener;
    //为多线程定义Handler
    private Handler handler = new Handler();
    int totalValue;

    public VideoPlayView(Context context) {
        super(context);
        init();
    }

    public VideoPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        setSurfaceTextureListener(surfaceTextureListener);
    }

//    private class PlayerVideoThread extends Thread {
//        @Override
//        public void run() {
//            try {
//                mMediaPlayer = new MediaPlayer();
//                //把res/raw的资源转化为Uri形式访问(android.resource://)
////                Uri uri = Uri.parse("android.resource://com.github.davidji80.videoplayer/"+R.raw.ansen);
//                Uri uri = Uri.parse("https://yzhch.bchunk.com/taskvideo/f75ee501df1904bb4b2b552c564117ae.mp4");
//
//                //设置播放资源(可以是应用的资源文件／url／sdcard路径)
//                mMediaPlayer.setDataSource(getContext(), uri);
//
//                //设置渲染画板
//                mMediaPlayer.setSurface(surface);
////                mMediaPlayer.setLooping(true);
//                //设置播放类型
//                AudioAttributes.Builder attrBuilder = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                    attrBuilder = new AudioAttributes.Builder();
//                    //设置音频流的合适属性
//                    attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
//                    mMediaPlayer.setAudioAttributes(attrBuilder.build());
//                } else {
//                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                }
//                //播放完成监听
//                mMediaPlayer.setOnCompletionListener(onCompletionListener);
//                //预加载监听
//                mMediaPlayer.setOnPreparedListener(onPreparedListener);
//                //设置是否保持屏幕常亮
//                mMediaPlayer.setScreenOnWhilePlaying(true);
//                //同步的方式装载流媒体文件
//                mMediaPlayer.prepare();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public void setVideoPlayViewListener(VideoPlayViewListener videoPlayViewListener) {
        mVideoPlayViewListener = videoPlayViewListener;
    }

    /**
     * 定义TextureView监听类SurfaceTextureListener
     * 重写4个方法
     */
    private SurfaceTextureListener surfaceTextureListener = new SurfaceTextureListener() {

        /**
         * 初始化好SurfaceTexture后调用
         * @param surfaceTexture
         * @param i
         * @param i1
         */
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            surface = new Surface(surfaceTexture);
            //开启一个线程去播放视频
//            new PlayerVideoThread().start();
        }

        /**
         * 视频尺寸改变后调用
         * @param surfaceTexture
         * @param i
         * @param i1
         */
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        /**
         * SurfaceTexture即将被销毁时调用
         * @param surfaceTexture
         * @return
         */
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            surface = null;
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            return true;
        }

        /**
         * 通过SurfaceTexture.updateteximage()更新指定的SurfaceTexture时调用
         * @param surfaceTexture
         */
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    /**
     * 流媒体播放结束时回调类
     */
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {//播放完成
            if (mVideoPlayViewListener != null) {
                mVideoPlayViewListener.onCompletion(mediaPlayer);
                mVideoPlayViewListener.onProgress(0, totalValue);
            }
            //删除执行的Runnable 终止计时器
            handler.removeCallbacks(mTicker);
        }
    };

    /**
     * 当装载流媒体完毕的时候回调
     */
    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            totalValue = mp.getDuration();
            if (mVideoPlayViewListener != null) {
                mVideoPlayViewListener.onPrepared(mp);
            }

//            //隐藏图片
//            videoImage.setVisibility(View.GONE);
//            //开始播放
//            mMediaPlayer.start();
//            //设置总进度
//            seekBar.setMax(mMediaPlayer.getDuration());
//            Log.e(Tag + "Duration", Integer.toString(mMediaPlayer.getDuration()));
            //用线程更新进度
            handler.post(mTicker);

        }
    };


    /**
     * 定义一个Runnable对象
     * 用于更新播发进度
     */
    private final Runnable mTicker = new Runnable() {
        @Override
        public void run() {
            //延迟200ms再次执行runnable,就跟计时器一样效果
            handler.postDelayed(mTicker, 200);

            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                //更新播放进度
                if (mVideoPlayViewListener != null) {
                    mVideoPlayViewListener.onProgress(mMediaPlayer.getCurrentPosition(), totalValue);
                }
            }
        }
    };
}
