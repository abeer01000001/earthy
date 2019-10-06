package com.example.ar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.animation.ModelAnimator;
import com.google.ar.sceneform.rendering.AnimationData;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {
    private ArFragment arFragment;
    private AnchorNode anchorNode;
    private ModelAnimator animator;
    private int nextAnimator;
    private FloatingActionButton btn_anim;
    private ModelRenderable animationCrab;
    private TransformableNode transformableNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arFragment = (ArFragment) getSupportFragmentManager()
                .findFragmentById(R.id.sceneform_fragment);
        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                if (animationCrab == null)
                    return;
                Anchor anchor = hitResult.createAnchor();
                if (anchorNode == null) {
                    anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());
                    transformableNode = new TransformableNode(arFragment.getTransformationSystem());
                    transformableNode.getScaleController().setMinScale(0.09f);
                    ;
                    transformableNode.getScaleController().setMaxScale(0.1f);
                    transformableNode.setParent(anchorNode);
                    transformableNode.setRenderable(animationCrab);

                }
            }
        });
        arFragment.getArSceneView().getScene()
                .addOnUpdateListener(new Scene.OnUpdateListener() {
                    @Override
                    public void onUpdate(FrameTime frameTime) {
                        if(anchorNode==null){
                            if (btn_anim.isEnabled()){
                                btn_anim.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                                btn_anim.setEnabled(false);
                            }
                        }else {
                            if (!btn_anim.isEnabled()){
                                btn_anim.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this,R.color.colorAccent));
                                btn_anim.setEnabled(true);
                            }
                        }
                    }
                });
        btn_anim = (FloatingActionButton)findViewById(R.id.btn_anim);
        btn_anim.setEnabled(false);
        btn_anim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(animator == null || !animator.isRunning()){
                    AnimationData data = animationCrab.getAnimationData(nextAnimator);
                    nextAnimator = (nextAnimator+1)%animationCrab.getAnimationDataCount();
                    animator = new ModelAnimator(data,animationCrab);
                    animator.start();
                }
            }
        });
        setupModel();
    }
    private  void setupModel(){
       ModelRenderable.builder()
               .setSource(this, R.raw.treexport)
                .build()
                .thenAccept(renderable -> animationCrab = renderable);

    }
}
