package fi.tiko.eatnyeet;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Highscore screen, lists top 10 highscores and allows user to change player name
 */
public class HighScoreScreen implements HighScoreListener, Screen {
	private Stage stage;
	private Skin skin;
	String langName;
	String langHighscores;
	String langUpdateButton;
	String langBackButton;
	InputMultiplexer multiplexer;
	InputAdapter gameUiInputs;
	public ArrayList<Button> buttons;
	private Table content;
	public static Texture startScreenBackGround;
	SpriteBatch batch;
	MainGame mainGame;

	/**
	 * Constructor. Shows images to user. The images shown are chosen by localization and language chosen by the user.
	 * @param batch saved for rendering
	 * @param mainGame saved to be able access other classes and information
	 */
    public HighScoreScreen (SpriteBatch batch, MainGame mainGame) {
		this.batch = batch;
		this.mainGame = mainGame;
		I18NBundle lang = I18NBundle.createBundle(Gdx.files.internal("lang"), mainGame.locale);
		langName = lang.get("name");
		langHighscores = lang.get("highscores");
		langBackButton = lang.get("back");
		langUpdateButton = lang.get("update");
		BackToMainMenuButton.backButtonTexture = new Texture(langBackButton);
		UpdateNameButton.buttonTexture = new Texture(langUpdateButton);
        HighScoreServer.readConfig("highscore.config");
        HighScoreServer.setVerbose(true);
        HighScoreServer.fetchHighScores(this);
		startScreenBackGround = new Texture("menu_background.png");
		buttons = new ArrayList<>();
		buttons.add(new BackToMainMenuButton(mainGame));
		buttons.add(new UpdateNameButton(mainGame));

        otherSetup();
    }
	@Override
	public void show() {

	}

	@Override
	public void hide() {

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

	/**
	 * Method which calls for updateScores method and passes HighsScoreEntry-List as an argument
	 * @param highScores the loaded highScoreEntry data in a List
	 */
	@Override
	public void receiveHighScore(List<HighScoreEntry> highScores) {
		Gdx.app.log("HighScoreScreen", "Received new high scores successfully");
		updateScores(highScores);
	}

	/**
	 * Method which fetches HighScores
	 * @param httpResponse
	 */
	@Override
	public void receiveSendReply(Net.HttpResponse httpResponse) {
		Gdx.app.log("HighScoreScreen", "Received response from server: "
				+ httpResponse.getStatus().getStatusCode());
		HighScoreServer.fetchHighScores(this);
	}

	@Override
	public void failedToRetrieveHighScores(Throwable t) {
		Gdx.app.error("HighScoreScreen",
				"Something went wrong while getting high scores", t);
	}

	@Override
	public void failedToSendHighScore(Throwable t) {
		Gdx.app.error("HighScoreScreen",
				"Something went wrong while sending a high scoreField entry", t);
	}

	/**
	 * Sets up the json skin. Calls for other methods in class
	 */
	private void otherSetup() {
		skin = new Skin();
		skin = new Skin (Gdx.files.internal("uiskin.json"));
		stage = new Stage();
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(stage);
		createGameUiInputs();
		multiplexer.addProcessor(gameUiInputs);
		Gdx.input.setInputProcessor(multiplexer);
		content = new Table();
		createTable();
		stage.addActor(content);
	}

	private ArrayList<Label> scoreLabels;

	/**
	 * Updates highscores
	 * @param scores List of highscores
	 */
	private void updateScores(List<HighScoreEntry> scores) {
		int i = 0;
		for (HighScoreEntry e : scores) {
			String entry = e.getScore() + " - " + e.getName();
			scoreLabels.get(i).setText(entry);
			i++;
		}
	}

	private TextField nameField;

	/**
	 * Creates a table
	 */
	private void createTable() {
		content.setFillParent(true);
		nameField = new TextField(mainGame.playerName, skin);
		content.add(new Label(" ", skin));
		content.row();
		content.add(nameField);
		content.row();
		content.add(new Label(langHighscores, skin)).colspan(2);
		scoreLabels = new ArrayList<>();
		for (int n = 0; n < 10; n++) {
			content.row();
			Label l = new Label("", skin);
			content.add(l).colspan(2);
			scoreLabels.add(l);
		}
		content.row();
	}

	/**
	 * Fetches HighScores
	 */
	private void fetchHighScores() {
		HighScoreServer.fetchHighScores(this);
	}

	/**
	 * Sends new highscores
	 */
	protected void createNewScore() {
		String name = mainGame.playerName;
		try {
			 name = nameField.getText();
			 mainGame.playerName = name;
		} catch (Exception e) {
			// could give out message for user that field cannot be empty
		}

		int score = mainGame.highestScore;
		HighScoreEntry scoreEntry = new HighScoreEntry(name, score);
		HighScoreServer.sendNewHighScore(scoreEntry, this);
		fetchHighScores();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		updateButtons();
		batch.begin();
		batch.draw(startScreenBackGround,0f,0f);
		renderButtons(batch);
		batch.end();
		stage.draw();
	}


	/***
	 * Ui inputs for font camera aka real pixel size camera
	 * Checks if any button is pressed, if is then resize them bigger to visualise click.
	 * Set clicked to true which triggers button built in actions.
	 */
	public void createGameUiInputs() {
		gameUiInputs = new InputAdapter() {
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				Vector3 realMousePos = new Vector3(screenX, screenY, 0);
				mainGame.fontCamera.unproject(realMousePos);

				float mousePosY = realMousePos.y;
				float mousePosX = realMousePos.x;

				for (Button btn : buttons) {
					if (mousePosX >= btn.getxStart() && mousePosX <= btn.getxEnd() && mousePosY >= btn.getyStart() && mousePosY <= btn.getyEnd()) {
						btn.setScale(1.2f);
					}
				}
				return true;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				Vector3 realMousePos = new Vector3(screenX, screenY, 0);
				mainGame.fontCamera.unproject(realMousePos);

				float mousePosY = realMousePos.y;
				float mousePosX = realMousePos.x;

				for (Button btn : buttons) {
					if (mousePosX >= btn.getxStart() && mousePosX <= btn.getxEnd() && mousePosY >= btn.getyStart() && mousePosY <= btn.getyEnd()) {
						btn.setScale(1f);
						btn.clicked();
					} else {
						btn.setScale(1f);
					}
				}
				return true;
			}
		};
	}

	/**
	 * Updates all buttons
	 */
	public void updateButtons() {
		for (fi.tiko.eatnyeet.Button obj: buttons) {
			obj.update();
		}
	}

	/**
	 * Renders all buttons
	 * @param batch saved for rendering
	 */
	public void renderButtons(SpriteBatch batch) {
		for (Button obj: buttons) {
			obj.render(batch);
		}
	}

	@Override
	public void dispose () {
		skin.dispose();
	}
}