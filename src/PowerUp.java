import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class PowerUp extends Part {
    private final Consumer<Square> effect;

    public Consumer<Square> getEffect() {
        return effect;
    }

    @Override
    public void handleCollisions(CollisionGroup group) {
        List<Part> collisions = group.getOverlappingParts(this);
        for (Part part: collisions) {
            if (!(part instanceof Square square)) continue;
            if (square.getHealth() == 0 || square.getWidth() == 0 || square.getHeight() == 0) continue;

            if (isSoundEnabled()) {
                try {
                    File file = new File("sounds/energy.wav");

                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);

                    clip.start();
                } catch (Exception ignored) {
                }
            }

            effect.accept(square);

            this.setSize(0, 0);
        }
    }

    public PowerUp(Consumer<Square> effect, Color backgroundColor, Color borderColor) {
        super(backgroundColor, borderColor);

        this.setSize(25, 25);
        this.effect = effect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PowerUp powerUp)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getEffect(), powerUp.getEffect());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEffect());
    }
}
