alter table qs add column version integer;
alter table qs add column lease_holder char(64);
alter table qs add column lease_started_at datetime;
alter table qs add column lease_ends_at datetime;