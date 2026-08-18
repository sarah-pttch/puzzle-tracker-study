grant select, insert on public.puzzles to authenticated;

create policy "Authenticated users can read puzzles"
on puzzles for select
to authenticated
using (true);

create policy "Authenticated users can submit puzzles"
on puzzles for insert
to authenticated
with check (true);