create type puzzle_status as enum ('personal', 'pending_verification', 'verified', 'rejected');

create table public.puzzles (
    id uuid primary key default gen_random_uuid(),
    name text not null,
    barcode text,
    piece_count integer,
    manufacturer text,
    publication_year integer,
    status puzzle_status not null default 'personal',
    submitted_by uuid references auth.users(id),
    created_at timestamptz not null default now()
);