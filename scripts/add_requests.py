from google.cloud import firestore
import argparse
import datetime
import names
import random

script_version=1

def queryUsers(db):
  users_ref = db.collection(u'users')
  docs = users_ref.get()
  docList = list()
  for doc in docs:
    docList.append(doc)
  return docList

def queryRequests(db):
  requests_ref = db.collection(u'requests')
  docs = requests_ref.get()
  docList = list()
  for doc in docs:
    docList.append(doc)
  return docList

def printSnapshot(doc):
  print(u'Created {} => {}'.format(doc.id, doc.to_dict()))


def existsRequest(requests, userA, userB):
  for request in requests:
    sender = request.get(u'senderId')
    receiver = request.get(u'receiverId')
    if (userA.id == sender and userB.id == receiver) or (userB.id == sender and userA.id == receiver):
      return True
  return False

def createRequest(db, sender, receiver):
  doc_ref = db.collection(u'requests').document()
  unread = random.choice([True, False])
  accepted = random.choice([True, False, False])
  acceptedTimestamp = None
  if accepted:
    acceptedTimestamp = datetime.datetime.now()
  doc_ref.set({
    u'senderId': sender.id,
    u'receiverId': receiver.id,
    u'sentTimestamp': datetime.datetime.now(),
    u'unread': unread,
    u'accepted': accepted,
    u'acceptedTimestamp': acceptedTimestamp,
    u'createdByScript': True,
    u'scriptVersion': script_version
  })
  doc = doc_ref.get()
  return doc

# very sub-optimal (but it's just a script)
def addRandomRequest(db, users, requests):
  # try upto 50 times
  for i in range(50):
    userA = random.choice(users)
    userB = random.choice(users)
    request = _addRequest(db, userA, userB, requests)
    if request is None:
      continue
    return request

def addUserRequest(db, user, users, requests):
  # try upto 50 times
  for i in range(50):
    otherUser = random.choice(users)
    request = _addRequest(db, user, otherUser, requests)
    if request is None:
      continue
    return request

def _addRequest(db, userA, userB, requests):
  if userA.id == userB.id:
    return None
  if existsRequest(requests, userA, userB):
    return None

  # randomize in case the user was specified
  users = [userA, userB]
  random.shuffle(users)
  return createRequest(db, users[0], users[1])

def getUser(userId, users):
  for user in users:
    if user.id == userId:
      return user
  return None

if __name__ == '__main__':
  parser = argparse.ArgumentParser()
  parser.add_argument("-c", "--count", type=int, default=3)
  parser.add_argument("--user")
  args = parser.parse_args()
  db = firestore.Client()

  users = queryUsers(db)
  requests = queryRequests(db)
  for i in range(0, args.count):
    if args.user is None:
      request = addRandomRequest(db, users, requests)
    else:
      request = addUserRequest(db, getUser(args.user, users), users, requests)      
    if request is None:
      print("Adding a request failed at count:" + str(i))
      break
    requests.append(request)
    printSnapshot(request)


  # Uncomment to query all users
  # queryUsers(db)
