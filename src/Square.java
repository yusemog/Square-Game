import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.File;

public class Square extends Part {
    public static final int healthRatio = 10;
    private final int minimumSize = 15;
    private final Color damagedColor;

    private int attackPower = 1;

    private boolean invincible = false;

    private int health;

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;

        this.setSize(minimumSize + health * healthRatio, minimumSize + health * healthRatio);
    }

    public void damage(int d) {
        this.setHealth(getHealth() - d);

        if (health <= 0) {
            this.setSize(0, 0);

            try {
                File file = new File("sounds/gong.wav");

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);

                clip.start();
            } catch (Exception ignored) {
            }

            this.setSoundEnabled(false);
        }

        if (isInvincible()) return;
        new Thread(() -> {
            Color backgroundColor = getBackground();
            setBackground(damagedColor);

            try {
                Thread.sleep(100);
            } catch (Exception ignored) {
            }

            setBackground(backgroundColor);
        }).start();
    }

    public Square(Color backgroundColor, Color damagedColor, Color borderColor) {
        super(backgroundColor, borderColor);

        this.damagedColor = damagedColor;

        setImmovable(false);
    }
}
