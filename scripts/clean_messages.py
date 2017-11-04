from google.cloud import firestore
import argparse
import datetime
import helpers

script_version=1

def deleteMessage(db, message):
  doc_ref = db.collection(u'messages').document(message.id)
  helpers.printSnapshot(doc_ref.get())
  doc_ref.delete()


if __name__ == '__main__':
  parser = argparse.ArgumentParser()
  args = parser.parse_args()
  db = firestore.Client()

  users = helpers.queryUsers(db)
  messages = helpers.queryMessages(db)
  for message in messages:
    user1 = helpers.getUser(message.get(u'user1'), users)
    user2 = helpers.getUser(message.get(u'user2'), users)

    if user1 is None or user2 is None:
      deleteMessage(db, message)

