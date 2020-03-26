package fi.tiko.eatnyeet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Manifold;

public class Field extends GameObject {

    // temp
    public static Texture empty;
    public static Texture fill1;
    public static Texture fill2;
    public static Texture fill3;
    public static Texture fill4;
    public static Texture fill5;
    public static Texture fill6;
    public static Texture fill7;
    public static Texture fill8;
    public static Texture fill9;
    float fillLevel;
    float maxFill = 10f;
    float timeWhenPreviousCrop;

    public Field(float width, float height, Body body , MainGame game) {
        super(empty, width,height, body, game);
        fillLevel = 10f;
        timeWhenPreviousCrop = 0f;
    }

    @Override
    public void update () {
        super.update();

        float currentPercent = fillLevel / maxFill;

        if (currentPercent > 0.05f) {
            cropCrops();
        }

        if (currentPercent < 0.1) {
            this.setTexture(empty);
        } else if (currentPercent < 0.2) {
            this.setTexture(fill1);
        } else if (currentPercent < 0.3) {
            this.setTexture(fill2);
        } else if (currentPercent < 0.4) {
            this.setTexture(fill3);
        } else if (currentPercent < 0.5) {
            this.setTexture(fill4);
        }else if (currentPercent < 0.6) {
            this.setTexture(fill5);
        }else if (currentPercent < 0.7) {
            this.setTexture(fill6);
        }else if (currentPercent < 0.8) {
            this.setTexture(fill7);
        }else if (currentPercent < 0.9) {
            this.setTexture(fill8);
        } else {
            this.setTexture(fill9);
        }
        //System.out.println("Current% " + currentPercent + ", fill " + fillLevel + ", max " + maxFill);


    }

    public void cropCrops() {
        
        if (lifeTime - timeWhenPreviousCrop > 5f) {
            timeWhenPreviousCrop = lifeTime;
            fillLevel -= 1f;
            throwBanana();
System.out.println(fillLevel);
            //temporal rat spawn
            if (fillLevel % 6 == 0.0f) {
                spawnRat();
            }
        }
    }
    public void spawnRat() {
        System.out.println("RAT");
        callAfterPhysicsStep(() -> {
            float posY = 1f;
            float posX = 2f;


            Rat temp = new Rat(posX, posY, game);
            //temp.body.setLinearVelocity(randX,randY);
            temp.body.setGravityScale(0.4f);
            game.gameObjects.add(temp);

            return null;
        });
    }

    public void throwBanana() {
        callAfterPhysicsStep(() -> {
            float fieldPosY = body.getPosition().y + 0.4f;
            float fieldPosX = body.getPosition().x;

            float randY = MathUtils.random(4f,8f);
            float randX = MathUtils.random(-2f,-0.5f);

            int tempN = MathUtils.random(1,3);
            if (tempN == 1) {
                Banana temp = new Banana(fieldPosX, fieldPosY, game);
                temp.body.setLinearVelocity(randX,randY);
                temp.body.setGravityScale(0.4f);
                game.gameObjects.add(temp);
            }
            else if (tempN == 2) {
                Tomato temp = new Tomato(fieldPosX, fieldPosY, game);
                temp.body.setLinearVelocity(randX,randY);
                temp.body.setGravityScale(0.4f);
                game.gameObjects.add(temp);
            }
            else if (tempN == 3) {
                Carrot temp = new Carrot(fieldPosX, fieldPosY, game);
                temp.body.setLinearVelocity(randX,randY);
                temp.body.setGravityScale(0.4f);
                game.gameObjects.add(temp);
            }
            //Banana temp = new Banana(fieldPosX, fieldPosY, game);



            return null;
        });
    }
    @Override
    public void onCollision(Contact contact, GameObject other) {

        if (other != null && other instanceof CompostWaste && other instanceof FlingableObject) {
            game.toBeDeleted.add(other);


            // if character carries the object to field this will reset character object reference and booleans
            if (((CompostWaste) other).isBeingCarried) {
                callAfterPhysicsStep(() -> {
                    ((CompostWaste) other).isBeingCarried = false;
                    game.player.resetObjectToCarry();
                    return null;
                });
            }
            if (fillLevel >= maxFill) {
                //System.out.println("Field already full!!");
            } else {
                fillLevel += ((CompostWaste) other).getFillAmount();
                game.player.characterScore += (int) ((CompostWaste) other).flyTime * game.player.characterCombo;
                game.player.characterCombo += 1;
            }

            //System.out.println("Field fillevel = " + fillLevel);
        }



    }
}
