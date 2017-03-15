use jerkoff;

db.createCollection('Jerkoff_lern');

//this?
db.Jerkoff_lern.createIndex( { "signature": 1,  "args":2 },{ unique: true } );

db.createUser(
  {
    user: "fratta",
    pwd: "fratta",
    roles: [ { role: "userAdmin", db: "jerkoff" } ]
  }
);