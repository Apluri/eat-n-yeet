package fi.tiko.eatnyeet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;

public class QuitToMainMenuButton extends Button {

    public static Texture quitButtonTexture;

    public QuitToMainMenuButton (MainGame mainGame) {
        super(quitButtonTexture,quitButtonTexture.getWidth(),quitButtonTexture.getHeight(),mainGame);
        setPosition(mainGame.FONT_CAM_WIDTH / 2f - getWidth() / 2f, mainGame.FONT_CAM_HEIGHT  - 500f - getHeight() / 2f);
        xStart = getX();
        xEnd = getX() + getWidth();
        yStart = getY();
        yEnd = getY() + getHeight();

    }

    @Override
    public void update () {
        super.update();

        // when clicked play button start game
        if (isClicked) {
            // highscore screenhere
            System.out.println("?");
            isClicked = false;
            //mainGame.gameScreen.dispose();
            //mainGame.startScreen.dispose();

            mainGame.startScreen = new StartScreen(mainGame.batch,mainGame);
            mainGame.setScreen(mainGame.startScreen);
        }
    }
}