import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SquareGame extends JFrame {
    public static final int arenaWidth = 400;
    public static final int arenaHeight = 400;
    public static final int borderSize = 5;
    public static final int initialHealth = 10;
    public static final int powerUpTimerDelay = 50;

    private boolean isGameOver = false;

    private final PowerUp[] powerUps;

    private final JPanel arena = new JPanel();
    private final CollisionGroup collisionGroup;

    public boolean isGameOver() {
        return isGameOver;
    }

    public void start() {
        this.setVisible(true);
        arena.setLayout(null);

        for (Part part: collisionGroup) {
            if (!List.of(arena.getComponents()).contains(part)) arena.add(part);
            if (!(part instanceof Square square)) continue;

            square.setHealth(initialHealth);

            square.setLocation(
                    (int) (Math.random() * (arenaWidth - square.getWidth())) + borderSize,
                    (int) (Math.random() * (arenaHeight - square.getHeight())) + borderSize
            );

            while (!collisionGroup.getOverlappingParts(part).isEmpty())
                square.setLocation(
                    (int) (Math.random() * (arenaWidth - square.getWidth())) + borderSize,
                    (int) (Math.random() * (arenaHeight - square.getHeight())) + borderSize
                );

            while (square.getVelocityX() == 0 || square.getVelocityY() == 0)
                square.setVelocity(
                        (int) (Math.random() * 10) - 5,
                        (int) (Math.random() * 10) - 5
                );
        }

        AtomicInteger delay = new AtomicInteger(powerUpTimerDelay);
        final int max = delay.get();

        Timer handling = new Timer(15, e -> {
            boolean isGameOver = false;
            int numberOfPlayers = 0;
            Square winner = null;
            for (Part o : collisionGroup) {
                if (o instanceof Square s) {
                    if (s.getHealth() > 0) {
                        winner = s;

                        numberOfPlayers++;
                    }
                }
            }

            if (numberOfPlayers == 1) this.isGameOver=true;

            if (numberOfPlayers == 1 && winner.getHealth() < 40) {
                for (int i = collisionGroup.size() - 1; i >= 0; i--) {
                    Part o = collisionGroup.get(i);
                    if (o instanceof PowerUp p) {
                        collisionGroup.remove(p);
                        arena.remove(p);
                    }
                }

                winner.setSoundEnabled(false);
                winner.setLocation(winner.getX() - 5, winner.getY() - 5);

                winner.setBackground(winner.getOriginalBackground());
                winner.damage(-1);
                winner.setBorder(BorderFactory.createLineBorder(winner.getBorderColor(), borderSize));
            } else if (numberOfPlayers == 0) {
                for (int i = collisionGroup.size() - 1; i >= 0; i--) {
                    Part o = collisionGroup.get(i);
                    if (o instanceof PowerUp p) {
                        collisionGroup.remove(p);
                        arena.remove(p);
                    }
                }

                isGameOver = true;
            }

            if (numberOfPlayers == 1 && winner.getHealth() >= 40) {
                winner.setSize(400, 400);
                winner.setLocation(borderSize, borderSize);
                winner.setVelocity(0, 0);
            }

            for (int i = 0; i < collisionGroup.size(); i++) {
                Part collision = collisionGroup.get(i);

                collision.handleCollisions(collisionGroup);
                collision.updatePosition();

                if (!(collision instanceof Square square)) continue;
                if (square.getHealth() <= 0) {
                    arena.remove(square);
                    square.setSize(0, 0);
                }
            }

            if (delay.decrementAndGet() == 0 && powerUps.length > 0 && !isGameOver) {
                PowerUp sel = powerUps[(int) (Math.random() * powerUps.length)];
                if (sel != null) {
                    PowerUp p = new PowerUp(sel.getEffect(), sel.getBackground(), sel.getBorderColor());

                    p.setSoundEnabled(sel.isSoundEnabled());
                    collisionGroup.add(p);
                    arena.add(p);

                    p.setVisible(true);

                    p.setLocation(borderSize + (int) (Math.random() * (arenaWidth - p.getWidth())),
                            borderSize + (int) (Math.random() * (arenaHeight - p.getHeight())));

                    while (!collisionGroup.getOverlappingParts(p).isEmpty())
                        p.setLocation(borderSize + (int) (Math.random() * (arenaWidth - p.getWidth())),
                            borderSize + (int) (Math.random() * (arenaHeight - p.getHeight())));

                    repaint();
                }

                delay.set(max);
            }
        });

        handling.start();
    }

    public SquareGame(CollisionGroup collisionGroup, PowerUp... powerUps) {
        super();

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.setResizable(false);

        this.getContentPane().setPreferredSize(new Dimension(9 * 50, 16 * 50));
        this.getContentPane().setBackground(Color.BLACK);

        this.pack();
        this.setLayout(null);

        arena.setSize(arenaWidth + borderSize * 2, arenaHeight + borderSize * 2);
        arena.setLocation(getContentPane().getWidth() / 2 - arena.getWidth() / 2, getContentPane().getHeight() / 2 - arena.getHeight() / 2);

        arena.setBackground(Color.BLACK);
        arena.setBorder(BorderFactory.createLineBorder(Color.WHITE, borderSize));

        arena.setLayout(null);

        this.add(arena);

        this.collisionGroup = collisionGroup;
        this.powerUps = powerUps;
    }
}
