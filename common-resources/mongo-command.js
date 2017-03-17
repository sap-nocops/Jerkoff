use
jerkoff;

db.createCollection('Jerkoff_lern');

db.Jerkoff_lern.createIndex({
	"signature" : 1,
	"argsBefore" : 2,
	"thisBefore" : 3
}, {
	unique : true
});

db.createUser({
	user : "fratta",
	pwd : "fratta",
	roles : [ {
		role : "userAdmin",
		db : "jerkoff"
	} ]
});