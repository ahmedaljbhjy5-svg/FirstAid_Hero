BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "CustomContacts" (
	"contact_id"	INTEGER,
	"contact_name"	TEXT,
	"phone_number"	INTEGER,
	PRIMARY KEY("contact_id")
);
CREATE TABLE IF NOT EXISTS "Emergencies" (
	"case_id"	INTEGER,
	"title"	TEXT,
	"danger_level"	TEXT,
	"symptoms"	TEXT,
	PRIMARY KEY("case_id")
);
CREATE TABLE IF NOT EXISTS "Steps" (
	"step_id"	INTEGER,
	"step_number"	INTEGER,
	"instruction_text"	INTEGER,
	"case_id"	INTEGER,
	PRIMARY KEY("step_id"),
	FOREIGN KEY("case_id") REFERENCES "Emergencies"("case_id")
);
CREATE TABLE IF NOT EXISTS "TreatedCases" (
	"treated_case_id"	INTEGER,
	"description"	TEXT,
	"case_date"	INTEGER,
	"photo_path"	TEXT,
	"procedures"	TEXT,
	PRIMARY KEY("treated_case_id")
);
COMMIT;
