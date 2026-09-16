# Sky-Book ✈️
A comprehensive, desktop-based Flight Reservation System built using Java and Swing. 

Sky-Book features a clean, modular UI, robust data persistence, and interactive user flows ranging from account registration to dynamic flight searching and graphical seat selection.

## 🚀 Features

- **Dynamic Flight Search & Autocomplete**: Start typing an origin or destination city and receive instant autocomplete suggestions. If no scheduled flights exist for a route, Sky-Book dynamically generates dummy flights on the fly so you can always proceed with testing!
- **Interactive Seat Map**: Visually select seats from a graphical cabin layout (Economy & Business class availability) before proceeding to checkout.
- **Robust Data Persistence**: Uses Java Serialization to save user accounts (`users.dat`) and flight bookings (`reservations.dat`) directly to your hard drive. Your data persists across application restarts!
- **Multi-Passenger Bookings**: Seamlessly add multiple passengers to a single reservation. The UI dynamically adjusts to collect individual names and passport numbers.
- **My Reservations Dashboard**: View all your booked flights, their statuses, and total costs. Cancel bookings with a single click, which automatically processes your refund, frees up the seats, and cleans up your history.
- **Authentication System**: Secure Login and Registration system. Supports standard users and Administrators.
- **Modular Architecture**: The codebase is strictly organized into standard Java packages (`model`, `ui`, `data`, `util`, `app`) making it highly scalable and easy to read.

## 🛠️ Tech Stack
- **Language**: Java
- **GUI Framework**: Java Swing & AWT
- **Data Storage**: Local binary files via Java Serialization (`java.io.Serializable`)

## 📦 How to Run

1. **Clone the repository:**
   ```bash
   git clone https://github.com/devanarayansanjoy/Sky-Book.git
   cd Sky-Book
   ```

2. **Compile the Java source files:**
   Ensure you have the JDK installed. Run the following command from the root directory:
   ```bash
   find src -name "*.java" > sources.txt && javac @sources.txt
   ```

3. **Run the Application:**
   ```bash
   java -cp src app.FlightReservationApp
   ```

4. **Testing:**
   - You can create a brand new account from the Login screen.
   - Or, log in with the default admin account:
     - **Username**: `admin`
     - **Password**: `admin123`

## 📂 Project Structure

```
Sky-Book/
├── src/
│   ├── app/                # Main Application Entry Point
│   ├── data/               # File Persistence & Databases
│   ├── model/              # Core Classes (User, Flight, Reservation)
│   ├── ui/                 # Swing Windows, Panels, and Dialogs
│   │   └── components/     # Custom Swing UI elements (Autocomplete)
│   ├── util/               # Helper utilities (Receipt Generator)
├── users.dat               # Persisted user accounts (generated on run)
└── reservations.dat        # Persisted flight bookings (generated on run)
```
