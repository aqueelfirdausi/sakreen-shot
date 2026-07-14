import sqlite3

conn = sqlite3.connect('sakreenshot_database')
cursor = conn.cursor()

cursor.execute('''
CREATE TABLE IF NOT EXISTS `screenshots` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `contentUri` TEXT NOT NULL,
    `mediaStoreId` INTEGER NOT NULL,
    `displayName` TEXT NOT NULL,
    `relativePath` TEXT,
    `extractedText` TEXT NOT NULL,
    `normalizedText` TEXT NOT NULL,
    `primaryCategory` TEXT NOT NULL,
    `classificationScore` INTEGER NOT NULL,
    `capturedAt` INTEGER NOT NULL,
    `indexedAt` INTEGER NOT NULL,
    `modifiedAt` INTEGER NOT NULL,
    `width` INTEGER,
    `height` INTEGER,
    `fileSize` INTEGER,
    `isPinned` INTEGER NOT NULL,
    `estimatedExpiry` INTEGER,
    `processingStatus` TEXT NOT NULL,
    `contentHash` TEXT
)
''')

cursor.execute('''
CREATE VIRTUAL TABLE IF NOT EXISTS `screenshots_fts` USING FTS4(
    `normalizedText` TEXT NOT NULL, content=`screenshots`
)
''')

cursor.execute('''
CREATE TABLE IF NOT EXISTS `room_master_table` (
    id INTEGER PRIMARY KEY,
    identity_hash TEXT
)
''')

# Insert some mock v1 rows (with duplicates)
cursor.execute('''
INSERT INTO screenshots (contentUri, mediaStoreId, displayName, relativePath, extractedText, normalizedText, primaryCategory, classificationScore, capturedAt, indexedAt, modifiedAt, isPinned, processingStatus)
VALUES ('content://1', 1001, 'test.jpg', null, 'test text', 'test text', 'UNSORTED', 100, 1000, 1000, 1000, 0, 'DONE')
''')
cursor.execute('''
INSERT INTO screenshots (contentUri, mediaStoreId, displayName, relativePath, extractedText, normalizedText, primaryCategory, classificationScore, capturedAt, indexedAt, modifiedAt, isPinned, processingStatus)
VALUES ('content://1', 1001, 'test.jpg', null, 'test text dup', 'test text dup', 'UNSORTED', 100, 1000, 1000, 1000, 0, 'DONE')
''')
cursor.execute('''
INSERT INTO screenshots (contentUri, mediaStoreId, displayName, relativePath, extractedText, normalizedText, primaryCategory, classificationScore, capturedAt, indexedAt, modifiedAt, isPinned, processingStatus)
VALUES ('content://2', 1002, 'test2.jpg', null, 'another text', 'another text', 'DOCUMENTS', 100, 2000, 2000, 2000, 1, 'DONE')
''')

# Provide the v1 identity hash Room expects.
# Wait, Room identity hash is checked by room_master_table. If we just don't have it, Room will throw an exception if the schema doesn't perfectly match the v1 json. 
# Alternatively, I can just build the v1 version of the app from a previous commit, install it, open it to generate the DB, and then upgrade to v2!
conn.commit()
conn.close()
