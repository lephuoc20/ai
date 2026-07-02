CREATE TABLE resources (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status varchar(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    creator_id VARCHAR(100) NOT NULL,
    creator_name VARCHAR(255) NOT NULL,
    tags TEXT[] NOT NULL DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_resources_creator_id ON resources (creator_id);
CREATE INDEX idx_resources_tags ON resources USING gin (tags);
