package demo.gl.eagleweb.xyz.colorshadowdemo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 一个颜色不断渐变的效果
 */
public class MainActivity extends AppCompatActivity {

    private RelativeLayout mActivityMainRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivityMainRelativeLayout = (RelativeLayout) findViewById(R.id.activity_main);
//        startAnim(Color.parseColor("#50B5EB"), Color.parseColor("#FF668A"));


        // 要达到效果 青色 -> 绿色 -> 蓝色 -> 紫色 -> 粉色 -> 青色 几个颜色来回变换！
        String[] colors = {"#00ffff", "#00ff00", "#0000ff", "#9900ff", "#ff66ff"};
        final ArrayList<Integer> integers = new ArrayList<>();
        for (int i = 0; i < colors.length; i++) {
            integers.add(Color.parseColor(colors[i]));
        }


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startAnim(integers, 1200);
            }
        }, 10, 1200 * integers.size());
    }

    /**
     * 开始渐变动画
     *
     * @param list     色彩变换集合
     * @param duration 每个色彩之间变换的时间
     */
    private void startAnim(List<Integer> list, int duration) {
        //                ==== 颜色渐变动画 =====
        final ArrayList<Animator> animators = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            int color = list.get(i);
            int color2;
            if (i == list.size() - 1) {
                color2 = list.get(0);
            } else {
                color2 = list.get(i + 1);
            }
            ValueAnimator valueAnimator = ValueAnimator.ofObject(typeEvaluator, color, color2);
            valueAnimator.setDuration(duration);
            valueAnimator.addUpdateListener(animatorUpdateListener);
            animators.add(valueAnimator);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(animators);
                animatorSet.start();
            }
        });
    }

    private void startAnim(int startColor, int endColor) {
        //                ==== 颜色渐变动画 =====
        ValueAnimator valueAnimator = ValueAnimator.ofObject(typeEvaluator, startColor, endColor);
        valueAnimator.setDuration(250);
        valueAnimator.addUpdateListener(animatorUpdateListener);

        ValueAnimator valueAnimator2 = ValueAnimator.ofObject(typeEvaluator, endColor, startColor);
        valueAnimator2.setDuration(250);
        valueAnimator2.addUpdateListener(animatorUpdateListener);

        ValueAnimator valueAnimator3 = ValueAnimator.ofObject(typeEvaluator, startColor, endColor);
        valueAnimator3.setDuration(250);
        valueAnimator3.addUpdateListener(animatorUpdateListener);

        ValueAnimator valueAnimator4 = ValueAnimator.ofObject(typeEvaluator, endColor, startColor);
        valueAnimator4.setDuration(250);
        valueAnimator4.addUpdateListener(animatorUpdateListener);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(valueAnimator, valueAnimator2, valueAnimator3, valueAnimator4);
        animatorSet.start();
    }


    TypeEvaluator typeEvaluator = new TypeEvaluator() {
        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            //从初始的int类型的颜色值中解析出Alpha、Red、Green、Blue四个分量
            int startInt = (Integer) startValue;
            int startA = (startInt >> 24) & 0xff;
            int startR = (startInt >> 16) & 0xff;
            int startG = (startInt >> 8) & 0xff;
            int startB = startInt & 0xff;

            //从终止的int类型的颜色值中解析出Alpha、Red、Green、Blue四个分量
            int endInt = (Integer) endValue;
            int endA = (endInt >> 24) & 0xff;
            int endR = (endInt >> 16) & 0xff;
            int endG = (endInt >> 8) & 0xff;
            int endB = endInt & 0xff;

            //分别对Alpha、Red、Green、Blue四个分量进行计算，
            //最终合成一个完整的int型的颜色值
            return (int) ((startA + (int) (fraction * (endA - startA))) << 24) |
                    (int) ((startR + (int) (fraction * (endR - startR))) << 16) |
                    (int) ((startG + (int) (fraction * (endG - startG))) << 8) |
                    (int) ((startB + (int) (fraction * (endB - startB))));
        }
    };

    ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(final ValueAnimator animation) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mActivityMainRelativeLayout != null) {
                        int color = (int) animation.getAnimatedValue();
                        mActivityMainRelativeLayout.setBackgroundColor(color);
                    }
                }
            });
        }
    };


}
