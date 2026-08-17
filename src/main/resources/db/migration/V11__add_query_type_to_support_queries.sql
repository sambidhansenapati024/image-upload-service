ALTER TABLE support_queries
ADD COLUMN query_type VARCHAR(30);

UPDATE support_queries
SET query_type = 'OTHER'
WHERE query_type IS NULL;

ALTER TABLE support_queries
ALTER COLUMN query_type SET NOT NULL;