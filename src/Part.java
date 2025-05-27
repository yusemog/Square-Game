import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class Part extends JPanel {
    private final Color borderColor;
    private int velocityX, velocityY;
    private boolean immovable = true;

    private boolean soundEnabled = true;

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    private final Color originalBackgroundColor;

    public Color getOriginalBackground() {
        return originalBackgroundColor;
    }

    public void handleCollisions(CollisionGroup group) {
        List<Part> collisions = group.getOverlappingParts(this);
        for (Part collision: collisions) {
            if (collision instanceof PowerUp) continue;
            if (collision.getWidth() == 0 || collision.getHeight() == 0) continue;

            Rectangle intersection = this.getBounds().intersection(collision.getBounds());

            if (!this.isImmovable() && !collision.isImmovable()) {
                int m1 = 5;
                int m2 = 5;

                int u1x = this.getVelocityX();
                int u1y = this.getVelocityY();
                int u2x = collision.getVelocityX();
                int u2y = collision.getVelocityY();

                int v1x = (2 * m2 * u2x) / (m1 + m2);
                int v1y = (2 * m2 * u2y) / (m1 + m2);
                int v2x = (2 * m1 * u1x) / (m1 + m2);
                int v2y = (2 * m1 * u1y) / (m1 + m2);

                this.setVelocity(v1x, v1y);
                collision.setVelocity(v2x, v2y);

                if (intersection.getWidth() < intersection.getHeight()) {
                    if (this.getX() < collision.getX()) {
                        this.setLocation(this.getX() - (int) intersection.getWidth(), this.getY());
                    } else {
                        this.setLocation(this.getX() + (int) intersection.getWidth(), this.getY());
                    }
                } else {
                    if (this.getY() < collision.getY()) {
                        this.setLocation(this.getX(), this.getY() - (int) intersection.getHeight());
                    } else {
                        this.setLocation(this.getX(), this.getY() + (int) intersection.getHeight());
                    }
                }

            } else {
                if (intersection.isEmpty()) continue;

                if (intersection.getWidth() > intersection.getHeight()) {
                    this.setVelocityY(-this.getVelocityY());
                } else {
                    this.setVelocityX(-this.getVelocityX());
                }
            }

            if (collision instanceof Square a && this instanceof Square b) {
                if (!a.isInvincible()) a.damage(b.getAttackPower());
                if (!b.isInvincible()) b.damage(a.getAttackPower());
            }

            if (soundEnabled){
                try {
                    File file = new File("sounds/hit.wav");

                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);

                    clip.start();
                } catch (Exception ignored) {
                }
            }
        }

        if ((getX() <= SquareGame.borderSize && velocityX < 0 ||
                getX() >= SquareGame.arenaWidth + SquareGame.borderSize - getWidth() && velocityX > 0 ||
                getY() <= SquareGame.borderSize && velocityY < 0 ||
                getY() >= SquareGame.arenaHeight + SquareGame.borderSize - getHeight() && velocityY > 0) && soundEnabled) {
            try {
                File file = new File("sounds/wall.wav");

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);

                clip.start();
            } catch (Exception ignored) {
            }
        }

        if (getX() <= SquareGame.borderSize && velocityX < 0 ||
                getX() >= SquareGame.arenaWidth + SquareGame.borderSize - getWidth() && velocityX > 0) {
            setVelocityX(-getVelocityX());
        }

        if (getY() <= SquareGame.borderSize && velocityY < 0 ||
                getY() >= SquareGame.arenaHeight + SquareGame.borderSize - getHeight() && velocityY > 0) {
            setVelocityY(-getVelocityY());
        }
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setImmovable(boolean immovable) {
        this.immovable = immovable;
    }

    public boolean isImmovable() {
        return immovable;
    }

    public boolean isColliding(Part o) {
        return o.getBounds().intersects(this.getBounds());
    }

    public void setVelocity(int velocityX, int velocityY) {
        this.setVelocityX(velocityX);
        this.setVelocityY(velocityY);
    }

    public void setVelocityX(int velocityX) {
        this.velocityX = velocityX;
    }

    public void setVelocityY(int velocityY) {
        this.velocityY = velocityY;
    }

    public int getVelocityX() {
        return velocityX;
    }

    public int getVelocityY() {
        return velocityY;
    }

    public Part(Color backgroundColor, Color borderColor) {
        super();

        this.originalBackgroundColor = backgroundColor;

        setBackground(backgroundColor);
        setBorder(BorderFactory.createLineBorder(borderColor, SquareGame.borderSize));

        this.borderColor = borderColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Part part)) return false;
        return part.getVelocityX() == getVelocityX() && part.getVelocityY() == getVelocityY();
    }

    @Override
    public String toString() {
        return String.format("Part[x=%d, y=%d, width=%d, height=%d, vx=%d, vy=%d]", getX(), getY(), getWidth(), getHeight(), getVelocityX(), getVelocityY());
    }

    public void updatePosition() {
        this.setLocation(getX() + getVelocityX(), getY() + getVelocityY());
    }
}
