package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.awt.Rectangle;
import java.util.Iterator;


public class MyGdxGame implements Screen {
	final Drop game;

	SpriteBatch batch;
	Texture dadImg;
	Texture moraImg;
	TextureRegion background;
	Rectangle dad;
	Sound dropSound;
	Music rainMusic;
	OrthographicCamera camera;
	long lastDropTime;
	Array<Rectangle> moras;
	int morasCount;

	public MyGdxGame(Drop game) {
		this.game = game;

		batch = new SpriteBatch();
		dadImg = new Texture(Gdx.files.internal("png_lodka_128.png"));
		moraImg = new Texture(Gdx.files.internal("zayac.png"));
		background = new TextureRegion(new Texture("images.jpg"), 0, 0, 800, 480);

		dropSound = Gdx.audio.newSound(Gdx.files.internal("bite_carrot_raw.mp3"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("water-boiling.mp3"));

		rainMusic.setLooping(true);
		rainMusic.play();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		dad = new Rectangle();
		dad.x = 800 / 2 - 64 / 2;
		dad.y = 20;
		dad.width = 64;
		dad.height = 64;
		moras = new Array<>();
		spawnRaindrop();
	}

	private void spawnRaindrop() {
		Rectangle mora = new Rectangle();
		mora.x = MathUtils.random(0, 800-64);
		mora.y = 480;
		mora.width = 64;
		mora.height = 64;
		moras.add(mora);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void render (float delta) {
		ScreenUtils.clear(1, 0, 0, 1);
		game.batch.begin();
		game.batch.draw(background, 0, 0, background.getRegionWidth(), background.getRegionHeight());
		for (Rectangle mora : moras) {
			game.batch.draw(moraImg, mora.x, mora.y);
		}
		game.batch.draw(dadImg, dad.x, dad.y);
		game.font.draw(game.batch, "Moras Collected: " + morasCount, 0, 480);
		game.font.setColor(0,0,0,0);
		game.batch.end();

		camera.update();

		batch.setProjectionMatrix(camera.combined);

		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			dad.x = (int) ((touchPos.x - 64) / 2);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) dad.x -= 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dad.x += 200 * Gdx.graphics.getDeltaTime();

		if (dad.x < 0) dad.x = 0;
		if (dad.x > 800 - 64) dad.x = 800 - 64;

		if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();


		Iterator<Rectangle> iter = moras.iterator();
		while (iter.hasNext()) {
			Rectangle moras = iter.next();
			moras.y -= 200 * Gdx.graphics.getDeltaTime();
			if (moras.y + 64 < 0) iter.remove();
			if (moras.intersects(dad)) {
				dropSound.play();
				iter.remove();
				morasCount++;
			}
			if (morasCount >= 5) {
				game.setScreen(new End(game));
			}
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
		dadImg.dispose();
		moraImg.dispose();
		dropSound.dispose();
		rainMusic.dispose();
	}

	@Override
	public void show() {

	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
		rainMusic.dispose();
	}
}