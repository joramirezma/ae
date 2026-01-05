-- V5__Add_seed_data.sql
-- Seed data for testing and development

-- Insert test users (password is 'password123' encoded with BCrypt)
INSERT INTO users (id, username, email, password) VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'admin', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsA1gN1Lx.gFtq9OiC'),
    ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'johndoe', 'john@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsA1gN1Lx.gFtq9OiC'),
    ('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'janedoe', 'jane@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsA1gN1Lx.gFtq9OiC')
ON CONFLICT (id) DO NOTHING;

-- Insert test projects
INSERT INTO projects (id, owner_id, name, status, deleted) VALUES
    ('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Admin Project 1', 'ACTIVE', false),
    ('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Admin Project 2 (Draft)', 'DRAFT', false),
    ('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a66', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'John Project 1', 'ACTIVE', false),
    ('10eebc99-9c0b-4ef8-bb6d-6bb9bd380a77', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'John Deleted Project', 'DRAFT', true),
    ('20eebc99-9c0b-4ef8-bb6d-6bb9bd380a88', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'Jane Project 1', 'DRAFT', false)
ON CONFLICT (id) DO NOTHING;

-- Insert test tasks
INSERT INTO tasks (id, project_id, title, completed, deleted) VALUES
    -- Tasks for Admin Project 1 (ACTIVE)
    ('30eebc99-9c0b-4ef8-bb6d-6bb9bd380a99', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 'Setup development environment', true, false),
    ('40eebc99-9c0b-4ef8-bb6d-6bb9bd380aaa', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 'Implement authentication', true, false),
    ('50eebc99-9c0b-4ef8-bb6d-6bb9bd380abb', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 'Write unit tests', false, false),
    -- Tasks for Admin Project 2 (DRAFT)
    ('60eebc99-9c0b-4ef8-bb6d-6bb9bd380acc', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55', 'Plan project architecture', false, false),
    -- Tasks for John Project 1 (ACTIVE)
    ('70eebc99-9c0b-4ef8-bb6d-6bb9bd380add', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a66', 'Create database schema', true, false),
    ('80eebc99-9c0b-4ef8-bb6d-6bb9bd380aee', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a66', 'Implement REST API', false, false),
    ('90eebc99-9c0b-4ef8-bb6d-6bb9bd380aff', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a66', 'Deleted task example', false, true),
    -- Tasks for Jane Project 1 (DRAFT)
    ('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380b11', '20eebc99-9c0b-4ef8-bb6d-6bb9bd380a88', 'Research technologies', false, false)
ON CONFLICT (id) DO NOTHING;

-- Insert audit log examples
INSERT INTO audit_logs (id, action, entity_id, timestamp) VALUES
    ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b22', 'CREATE_PROJECT', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', NOW() - INTERVAL '7 days'),
    ('c1eebc99-9c0b-4ef8-bb6d-6bb9bd380b33', 'ACTIVATE_PROJECT', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', NOW() - INTERVAL '6 days'),
    ('d1eebc99-9c0b-4ef8-bb6d-6bb9bd380b44', 'CREATE_TASK', '30eebc99-9c0b-4ef8-bb6d-6bb9bd380a99', NOW() - INTERVAL '5 days'),
    ('e1eebc99-9c0b-4ef8-bb6d-6bb9bd380b55', 'COMPLETE_TASK', '30eebc99-9c0b-4ef8-bb6d-6bb9bd380a99', NOW() - INTERVAL '4 days')
ON CONFLICT (id) DO NOTHING;
