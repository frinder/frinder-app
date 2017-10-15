from google.cloud import firestore
import argparse
import datetime
import names
import random

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

def createRequest(sender, receiver):
	doc_ref = db.collection(u'requests').document()
	unread = random.choice([True, False])
	accepted = random.choice([True, False, False])
	if accepted:
		acceptedTimestamp = datetime.datetime.now()
	doc_ref.set({
    u'senderId': sender.id,
    u'receiverId': receiver.id,
    u'sentTimestamp': datetime.datetime.now(),
	  u'unread': unread,
	  u'accepted': accepted,
	  u'acceptedTimestamp': acceptedTimestamp
	})
	doc = doc_ref.get()
	return doc

# very sub-optimal (but it's just a script)
def addRequest(db, users, requests):
	# try upto 50 times
	for i in range(50):
		userA = random.choice(users)
		userB = random.choice(users)
		if userA.id == userB.id:
			continue
		if existsRequest(requests, userA, userB):
			continue
		return createRequest(userA, userB)


if __name__ == '__main__':
	parser = argparse.ArgumentParser()
	parser.add_argument("-c", "--count", type=int, default=5)
	args = parser.parse_args()
	db = firestore.Client()

	users = queryUsers(db)
	requests = queryRequests(db)
	for i in range(0, args.count):
		request = addRequest(db, users, requests)
		if request is None:
			print("Adding a request failed at count:" + str(i))
			break
		requests.append(request)
		printSnapshot(request)


	# Uncomment to query all users
	# queryUsers(db)
