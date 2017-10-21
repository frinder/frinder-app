from google.cloud import firestore
import argparse
import datetime
import helpers

script_version=1

def deleteRequest(db, request):
  doc_ref = db.collection(u'requests').document(request.id)
  helpers.printSnapshot(doc_ref.get())
  doc_ref.delete()


if __name__ == '__main__':
  parser = argparse.ArgumentParser()
  args = parser.parse_args()
  db = firestore.Client()

  users = helpers.queryUsers(db)
  requests = helpers.queryRequests(db)
  for request in requests:
    sender = helpers.getUser(request.get(u'senderId'), users)
    receiver = helpers.getUser(request.get(u'receiverId'), users)

    if sender is None or receiver is None:
      deleteRequest(db, request)

