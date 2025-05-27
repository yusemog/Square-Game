import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class Main {
    public static void main(String[] args) {
        Square redSquare = new Square(new Color(200, 0, 0), new Color(255, 0, 0), new Color(155, 0, 0));
        redSquare.setName("Red");

        Square blueSquare = new Square(new Color(0, 0, 200), new Color(0, 0, 255), new Color(0, 0, 155));
        blueSquare.setName("Blue");

        Square greenSquare = new Square(new Color(0, 200, 0), new Color(0, 255, 0), new Color(0, 155, 0));
        greenSquare.setName("Green");

        CollisionGroup collisionGroup = new CollisionGroup(redSquare, blueSquare);

        Consumer<Square> healEffect = square -> {
            square.damage(-1);
            square.setLocation(square.getX() - Square.healthRatio / 2, square.getY() - Square.healthRatio / 2);
        };

        PowerUp healPowerUp = new PowerUp(healEffect, new Color(251, 139, 174), new Color(253, 84, 132));

        Consumer<Square> invincibleEffect = square -> {
            square.setBackground(new Color(249, 193, 2));

            new Thread(()->{
                square.setInvincible(true);

                Color invColor = new Color(249, 193, 2);

                int i = 0;
                while (i < 200) {
                    square.setInvincible(true);
                    square.setBackground(invColor);

                    i++;

                    try {
                        Thread.sleep(10);
                    } catch (Exception ignored) {
                    }
                }

                square.setInvincible(false);

                if (!square.isInvincible())
                    square.setBackground(square.getOriginalBackground());
            }).start();
        };

        PowerUp invinciblePowerUp = new PowerUp(invincibleEffect, new Color(239, 218, 52), new Color(249, 193, 2));

        SquareGame squareGame = new SquareGame(collisionGroup, invinciblePowerUp, healPowerUp);
        squareGame.start();
    }
}
