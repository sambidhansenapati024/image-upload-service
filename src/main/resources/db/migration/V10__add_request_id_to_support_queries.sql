ALTER TABLE support_queries
ADD COLUMN request_id UUID;

UPDATE support_queries
SET request_id = gen_random_uuid()
WHERE request_id IS NULL;

ALTER TABLE support_queries
ALTER COLUMN request_id SET NOT NULL;

ALTER TABLE support_queries
ADD CONSTRAINT uk_support_queries_request_id
UNIQUE (request_id);