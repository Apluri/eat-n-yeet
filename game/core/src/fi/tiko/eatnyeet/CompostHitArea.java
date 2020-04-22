package fi.tiko.eatnyeet;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;

public class CompostHitArea extends GameObject {
    public CompostHitArea(float width, float height, Body body , GameScreen game) {
        super(width,height, body, game);

        Filter filter = new Filter();
        filter.categoryBits = OTHER_BITS;
        filter.maskBits = FLINGABLE_BITS;
        for (Fixture fix: body.getFixtureList()) {
            fix.setFilterData(filter);
        }
    }

    @Override
    public void onCollision(Contact contact, GameObject other) {

        if (other != null && other instanceof Food && other instanceof FlingableObject) {
            game.toBeDeleted.add(other);
            // if character carries the object to compost this will reset character object reference and booleans
            if (((FlingableObject) other).isBeingCarried) {
                callAfterPhysicsStep(() -> {
                    ((FlingableObject) other).isBeingCarried = false;
                    game.player.resetObjectToCarry();
                    return null;
                });
            }

            if (game.compost.fillLevel >= game.compost.maxFill) {
                System.out.println("Field already full!!");
            } else {
                game.compost.fillLevel += ((FlingableObject) other).getFillAmount();
                game.player.characterScore += (int) ((FlingableObject) other).flyTime * game.player.characterCombo;
                game.player.characterCombo += 1;
            }


        }

    }
}