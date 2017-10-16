from google.cloud import firestore
import argparse
import datetime
import names
import random

genders = [u'male', u'female']
interests = [u'Movies', u'Football', u'Books', u'Music']
script_version=1

def queryUsers(db):
  users_ref = db.collection(u'users')
  docs = users_ref.get()

  for doc in docs:
    print(u'{} => {}'.format(doc.id, doc.to_dict()))


def addUser(db, ref_lat, ref_lon, range):
  doc_ref = db.collection(u'users').document()
  uid = doc_ref.id
  gender = random.choice(genders)
  lat = ref_lat + random.uniform(-range, range)
  lon = ref_lon + random.uniform(-range, range)
  doc_ref.set({
    u'desc': u'Test user created using a script',
    u'id': unicode(uid),
    u'name': names.get_full_name(gender=gender),
    u'linkUrl': u'https://www.google.com',
    u'email': u'fake@gmail.com',
    u'profilePicUrl': None,
    u'gender': gender,
    u'age': random.randint(14,80),
    u'timestamp': datetime.datetime.now(),
    u'interests': [random.choice(interests)],
    u'location': [lat, lon],
    u'createdByScript': True,
    u'scriptVersion': script_version
  })
  doc = doc_ref.get()
  print(u'Created {} => {}'.format(doc.id, doc.to_dict()))



if __name__ == '__main__':
  parser = argparse.ArgumentParser()
  parser.add_argument("-c", "--count", type=int, default=3)
  parser.add_argument("--lat", type=float, default=37.0)
  parser.add_argument("--lon", type=float, default=-122.0)
  parser.add_argument("--range", type=float, default=-0.000001)
  args = parser.parse_args()
  db = firestore.Client()


  for i in range(0, args.count):
    addUser(db, args.lat, args.lon, args.range)

  # Uncomment to query all users
  # queryUsers(db)
