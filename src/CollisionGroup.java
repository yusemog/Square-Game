import java.util.*;

public class CollisionGroup extends ArrayList<Part> {
    public List<Part> getOverlappingParts(Part part, Part... excludingParts) {
        List<Part> overlappingParts = new ArrayList<>();
        for (int i = 0; i < this.size(); i++) {
            Part o = this.get(i);

            if (part.equals(o) || List.of(excludingParts).contains(o)) continue;

            if (part.isColliding(o) || (part instanceof Square square && square.getHealth() > 0 && part.isColliding(o)))
                overlappingParts.add(o);
        }
        return overlappingParts;
    }

    public String[] getNames() {
        return this.stream().map(Part::getName).toArray(String[]::new);
    }

    public CollisionGroup(Part... parts) {
        super();

        if (parts == null) return;
        this.addAll(List.of(parts));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CollisionGroup cg)) return false;
        if (cg.size() != this.size()) return false;
        for (int i = 0; i < this.size(); i++) if (!cg.get(i).equals(this.get(i))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "CollisionGroup[parts="+super.toString()+"]";
    }
}
