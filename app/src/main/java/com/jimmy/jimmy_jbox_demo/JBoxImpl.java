package com.jimmy.jimmy_jbox_demo;

import android.view.View;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import java.util.Random;

public class JBoxImpl {

    private float density = 0.5f;
    private World world;
    private int width, height;
    private float mRatio = 50;//坐标映射比例

    private float dt = 1f / 60f; //模拟世界的频率
    private int mVelocityIterations = 5; //速率迭代器
    private int mPositionIterations = 20; //迭代次数
    private final Random mRandom = new Random();


    public JBoxImpl(float density) {
        this.density = density;
    }

    public void createWorld() {
        if (world == null) {
            world = new World(new Vec2(0, 10));
            updateVerticalBounds();
            updateHorizontalBounds();
        }
    }

    public void startWorld() {
        if (world != null) {
            world.step(dt, mVelocityIterations, mPositionIterations);
        }
    }

    public void setWorldSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void createBody(View view) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;

        Shape shape;
        Boolean isCircle = (Boolean) view.getTag(R.id.circle_tag_view);
        if (isCircle != null && isCircle) {
            shape = new CircleShape();
            shape.setRadius(switchPositionToBody(view.getWidth() / 2));

        } else {
            PolygonShape polygonShape = new PolygonShape();
            polygonShape.setAsBox(switchPositionToBody(view.getWidth()), switchPositionToBody(view.getHeight()));
            shape = polygonShape;
        }

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(shape);
        fixtureDef.density = density;
        fixtureDef.friction = 0.8f;//摩擦系数
        fixtureDef.restitution = 0.5f;//补偿系数

        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        view.setTag(R.id.body_tag, body);
        body.setLinearVelocity(new Vec2(mRandom.nextFloat(), mRandom.nextFloat()));
    }


    private void updateHorizontalBounds() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;

        PolygonShape shape = new PolygonShape();
        float shapeWidth = switchPositionToBody(mRatio);
        float shapeHeight = switchPositionToBody(height);
        shape.setAsBox(shapeWidth, shapeHeight);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.8f;//摩擦系数
        fixtureDef.restitution = 0.5f; //补偿系数

        bodyDef.setPosition(new Vec2(0, 0));
        Body topBody = world.createBody(bodyDef);
        topBody.createFixture(fixtureDef);

        bodyDef.setPosition(new Vec2(switchPositionToBody(width), 0));
        Body bottomBody = world.createBody(bodyDef);
        bottomBody.createFixture(fixtureDef);
    }

    private void updateVerticalBounds() {
        BodyDef bodyDef = new BodyDef();
//        bodyDef.setActive(false);
        bodyDef.type = BodyType.STATIC;

        PolygonShape polygonShape = new PolygonShape();
        float shapeWidth = switchPositionToBody(width);
        float shapeHeight = switchPositionToBody(mRatio);
        polygonShape.setAsBox(shapeWidth, shapeHeight);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        fixtureDef.shape = polygonShape;
        fixtureDef.friction = 0.8f;//摩擦系数
        fixtureDef.restitution = 0.5f; //补偿系数

        bodyDef.setPosition(new Vec2(0, 0));
        Body topBody = world.createBody(bodyDef);
        topBody.createFixture(fixtureDef);

        bodyDef.setPosition(new Vec2(0, switchPositionToBody(height)));
        Body bottomBody = world.createBody(bodyDef);
        bottomBody.createFixture(fixtureDef);


    }

    private float switchPositionToBody(float viewPosition) {
        return viewPosition / mRatio;
    }

    private float switchPositionToView(float bodyPosition) {
        return bodyPosition * mRatio;
    }

    public boolean isBodyView(View view) {
        Body isBody = (Body)view.getTag(R.id.body_tag);
        return isBody != null;

    }

    public float getViewX(View view) {
        Body body = (Body) view.getTag(R.id.body_tag);
        if (body != null) {
            return switchPositionToView(body.getPosition().x) - (view.getWidth() / 2);
        }
        return 0;
    }

    public float getViewY(View view) {
        Body body = (Body) view.getTag(R.id.body_tag);
        if (body != null) {
            return switchPositionToView(body.getPosition().y) - (view.getHeight() / 2);
        }
        return 0;
    }

    public float getViewRotation(View view) {
        Body body = (Body) view.getTag(R.id.body_tag);
        if (body != null) {
            float angle = body.getAngle();
            return (angle / 3.14f * 180f) % 360;
        }
        return 0;
    }

    public void applyLinearImpulse(float x, float y, View view) {
        Body body = (Body) view.getTag(R.id.body_tag);
        Vec2 impulse = new Vec2(x, y);
        body.applyLinearImpulse(impulse, body.getPosition(), true); //给body做线性运动 true 运动完之后停止
    }

}
