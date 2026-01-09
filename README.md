# Plants vs Zombies

A Java-based tower defense game inspired by the classic Plants vs Zombies.

## Download

**[Download for Windows](https://github.com/digrajkarmeetwork/plants-vs-zombies/releases/latest/download/PlantsVSZombies-Windows.zip)** - No Java installation required!

Or visit our [download page](https://digrajkarmeetwork.github.io/plants-vs-zombies/) for more information.

## Features

- **5 Plant Types**: Sunflower, Pea Shooter, Venus Flytrap, Walnut, and Potato
- **3 Zombie Types**: Generic Zombie, Frank the Tank, and Burrowing Bailey
- **Level Editor**: Create custom levels with your choice of zombies
- **Undo/Redo**: Reverse moves if you make a mistake
- **Save/Load**: Save your progress and continue later
- **Animated Sprites**: Each plant and zombie has animated sprites

## How to Play

1. Download and extract the ZIP file
2. Run `PlantsVSZombies.exe`
3. Click **Menu → Start** to begin
4. Select a plant from the left panel
5. Click on a grid cell to place it
6. Click **End Turn** to advance the game
7. Don't let zombies reach the left side!

## Plant Costs

| Plant | Sun Points | Description |
|-------|-----------|-------------|
| Sunflower | 50 | Generates sun points each turn |
| Pea Shooter | 100 | Shoots peas at zombies in its row |
| Venus Flytrap | 150 | Attacks zombies that get close |
| Walnut | 50 | High health, blocks zombies |
| Potato | 25 | Cheap defensive plant |

## System Requirements

- Windows 10/11 (64-bit)
- No Java installation required (bundled with game)
- ~150 MB disk space

## Building from Source

If you want to build the game yourself:

```bash
# Compile
javac -d build/classes -sourcepath src src/App.java src/controller/*.java src/model/*.java src/view/*.java

# Copy resources
cp -r resources build/classes/

# Create JAR
cd build/classes
jar cfe ../../dist/PlantsVSZombies.jar App .

# Run
java -jar dist/PlantsVSZombies.jar
```

## License

This project is for educational purposes.
