package eu.elektropolnilnice.igra.minigame_david.object;

public class Player {
    private String name;
    private Car car;

    public Player(String name, Car car) {
        this.name = name;
        this.car = car;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    @Override
    public String toString() {
        return "Player{" +
            "name='" + name + '\'' +
            ", car=" + car +
            '}';
    }
}
