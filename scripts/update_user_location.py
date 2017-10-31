from google.cloud import firestore
import argparse
import datetime
import helpers
import random


def updateLocation(db, user, ref_lat, ref_lon, range):
  doc_ref = db.collection(u'users').document(user.id)
  lat = ref_lat + random.uniform(-range, range)
  lon = ref_lon + random.uniform(-range, range)
  doc_ref.update({
    u'timestamp': datetime.datetime.utcnow(),
    u'location': [lat, lon]
  })
  doc = doc_ref.get()
  helpers.printSnapshot(doc)


if __name__ == '__main__':
  parser = argparse.ArgumentParser()
  parser.add_argument("--all")
  parser.add_argument("-c", "--count", type=int, default=0)
  parser.add_argument("--lat", type=float, default=37.0)
  parser.add_argument("--lon", type=float, default=-122.0)
  parser.add_argument("--range", type=float, default=-0.000001)
  args = parser.parse_args()
  db = firestore.Client()

  users = helpers.queryUsers(db)
  index = 0
  for user in users:
    userDict = user.to_dict()
    if args.all is not None or (u'createdByScript' in userDict and userDict.get(u'createdByScript')):
      updateLocation(db, user, args.lat, args.lon, args.range)
      index = index + 1
      if index >= args.count and args.count != 0:
        break