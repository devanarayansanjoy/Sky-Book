import os
import re
import shutil

# Target directory
SRC_DIR = 'src/main/java'

# Mapping of files to their new packages
file_packages = {
    'FlightReservationApp.java': 'com.skybook.main',
    'Flight.java': 'com.skybook.model',
    'Reservation.java': 'com.skybook.model',
    'User.java': 'com.skybook.model',
    'FlightDatabase.java': 'com.skybook.data',
    'UserDatabase.java': 'com.skybook.data',
    'LoginFrame.java': 'com.skybook.ui',
    'MainFrame.java': 'com.skybook.ui',
    'SearchPanel.java': 'com.skybook.ui',
    'BookingDialog.java': 'com.skybook.ui',
    'SeatMapDialog.java': 'com.skybook.ui',
    'PaymentDialog.java': 'com.skybook.ui',
    'ReservationsPanel.java': 'com.skybook.ui',
    'AdminPanel.java': 'com.skybook.ui',
    'AutoCompleteTextField.java': 'com.skybook.ui.components',
    'ReceiptGenerator.java': 'com.skybook.util',
}

# Special mapping for tracker
tracker_file = 'LiveFlightTracker/LiveFlightTracker.java'

# Ensure directories exist
for pkg in set(file_packages.values()):
    os.makedirs(os.path.join(SRC_DIR, pkg.replace('.', '/')), exist_ok=True)
os.makedirs(os.path.join(SRC_DIR, 'com/skybook/tracker'), exist_ok=True)

def process_file(src_path, dest_pkg, file_name):
    if not os.path.exists(src_path):
        print(f"File not found: {src_path}")
        return
        
    with open(src_path, 'r') as f:
        content = f.read()

    # Determine required imports based on class usage
    # We'll just add all other packages as wildcard imports if they are used, or just add all of them
    # Since it's a small app, adding a few wildcards is easiest.
    imports = [
        "import com.skybook.model.*;",
        "import com.skybook.data.*;",
        "import com.skybook.ui.*;",
        "import com.skybook.ui.components.*;",
        "import com.skybook.util.*;",
        "import com.skybook.main.*;"
    ]
    # Remove the import of its own package
    my_import = f"import {dest_pkg}.*;"
    if my_import in imports:
        imports.remove(my_import)

    import_str = "\n".join(imports)
    
    # If there are existing imports, insert after package, else just put at top
    # The file currently has no package statement.
    new_content = f"package {dest_pkg};\n\n{import_str}\n\n" + content
    
    dest_path = os.path.join(SRC_DIR, dest_pkg.replace('.', '/'), file_name)
    with open(dest_path, 'w') as f:
        f.write(new_content)
    
    print(f"Processed {file_name} -> {dest_path}")

for file_name, pkg in file_packages.items():
    process_file(file_name, pkg, file_name)

# Process LiveFlightTracker
process_file(tracker_file, 'com.skybook.tracker', 'LiveFlightTracker.java')
if os.path.exists('LiveFlightTracker/flight_map.html'):
    shutil.copy('LiveFlightTracker/flight_map.html', os.path.join(SRC_DIR, 'com/skybook/tracker/flight_map.html'))

# Delete original java files
for file_name in file_packages.keys():
    if os.path.exists(file_name):
        os.remove(file_name)
if os.path.exists('LiveFlightTracker'):
    shutil.rmtree('LiveFlightTracker')

# Delete class files
for root, dirs, files in os.walk('.'):
    for f in files:
        if f.endswith('.class') and not root.startswith(f'./{SRC_DIR}'):
            os.remove(os.path.join(root, f))
