package charactercard.charactercard.util.managers;

public enum NoteTypes {
    WHOLE(4),
    HALF(2),
    QUARTER(1),
    SIXTEENTH(1/2),
    QUARTER_REST(1);

    private double value;

    public double getValue() {
        return this.value;
    }

    private NoteTypes(double value) {
        this.value = value;
    }
}
