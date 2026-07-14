import sqlite3
import json
import sys

db_path = 'sakreenshot_database'

try:
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    cursor.execute("SELECT COUNT(*) FROM screenshots")
    count = cursor.fetchone()[0]
    
    if count == 0:
        print("MIGRATION FAILED: Table is empty.")
        sys.exit(1)
        
    print(f"MIGRATION SUCCESS: {count} rows survived.")
    
    # check index exists
    cursor.execute("PRAGMA index_list('screenshots')")
    indexes = cursor.fetchall()
    has_unique = any(idx[1] == 'index_screenshots_mediaStoreId' and idx[2] == 1 for idx in indexes)
    if has_unique:
        print("MIGRATION SUCCESS: UNIQUE index exists.")
    else:
        print("MIGRATION FAILED: UNIQUE index missing.")
        sys.exit(1)
except Exception as e:
    print(f"Error: {e}")
    sys.exit(1)
