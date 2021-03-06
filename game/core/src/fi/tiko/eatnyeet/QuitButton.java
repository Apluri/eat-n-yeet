package fi.tiko.eatnyeet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class QuitButton extends Button {

    public static Texture quitButtonTexture;

    /**
     * Constructor, creates button with default settings
     * @param mainGame saved to button superclass
     */
    public QuitButton (MainGame mainGame) {
        super(quitButtonTexture,quitButtonTexture.getWidth() * 0.8f,quitButtonTexture.getHeight() * 0.8f,mainGame);
        setPosition(mainGame.FONT_CAM_WIDTH / 2f - getWidth() / 2f, mainGame.FONT_CAM_HEIGHT  - 630f - getHeight() / 2f);
        xStart = getX();
        xEnd = getX() + getWidth();
        yStart = getY();
        yEnd = getY() + getHeight();

    }

    /**
     * Default update for buttons, methods that all buttons need to call will be added here. Calling superclass update is a must.
     */
    @Override
    public void update () {
        super.update();

        if (isClicked) {
            isClicked = false;
            Gdx.app.exit();
        }
    }
}
