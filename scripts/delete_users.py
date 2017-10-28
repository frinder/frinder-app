from google.cloud import firestore
import argparse
import datetime
import helpers

script_version=1

def deleteUser(db, user):
  doc_ref = db.collection(u'users').document(user.id)
  helpers.printSnapshot(doc_ref.get())
  doc_ref.delete()


if __name__ == '__main__':
  parser = argparse.ArgumentParser()
  parser.add_argument("-v", "--version", type=int, default=0)
  args = parser.parse_args()
  db = firestore.Client()

  users = helpers.queryUsers(db)
  for user in users:
    userDict = user.to_dict()
    if u'createdByScript' in userDict and userDict.get(u'createdByScript') and u'scriptVersion' in userDict and userDict.get(u'scriptVersion') == args.version:
      deleteUser(db, user)
