
def printSnapshot(doc):
  print(u'Created {} => {}'.format(doc.id, doc.to_dict()))

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

def getUser(userId, users):
  for user in users:
    if user.id == userId:
      return user
  return None