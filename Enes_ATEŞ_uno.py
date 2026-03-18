import random
class UnoCard:
    def __init__(self,c,n):
        self.color=c    
        self.number=n
        self.list=["Blue","Red","Green","Yellow"]
    def __str__(self):
        return self.list[self.color]+' '+str(self.number)
    def canPlay(self, other):
        if self.color==other.color or self.number==other.number:
            return True
        else:
            return False
class CollectionOfUnoCards:
	def __init__(self):
		self.list=[]
	def addCard(self,c):
		self.list.append(c)
	def makeDeck(self):
		self.list.clear()
		for color in range(0,4):
			for num in range(1,10):
				self.list.append(UnoCard(color,num))
				self.list.append(UnoCard(color,num))
	def shuffle(self):
		random.shuffle(self.list)
	def __str__(self):
		s=""
		for card in self.list:
			s+=str(card)+" "
		return s
	def getNumCards(self):
		return len(self.list)
	def getTopCard(self):
		return self.list[0]
	def getCard(self, index):
		return self.list[index]
class Uno:
    def __init__(self):
        self.deck=CollectionOfUnoCards()
        self.deck.makeDeck()
        self.hand1=CollectionOfUnoCards()
        self.hand2=CollectionOfUnoCards()
        self.deck.shuffle()
        for i in range (7):
            theCard=self.deck.list.pop()
            self.hand1.addCard(theCard)
        for i in range (7):
            theCard=self.deck.list.pop()
            self.hand2.addCard(theCard)
    def playGame(self):
        self.f=CollectionOfUnoCards()
        self.f.list=self.deck.list.copy()
        for i in range (self.hand2.getNumCards()-1,-1,-1):
            self.f.list.insert(0,self.hand2.list[i])
        for i in range (self.hand1.getNumCards()-1,-1,-1):
            self.f.list.insert(0,self.hand1.list[i])
        print("\n","First Deck:", self.f,"\n")
        self.y=self.deck.getTopCard()
        self.deck.list.remove(self.y)
        print("Cards are dealing...","\n")
        print("Player1 is firstly starting...","\n")
        self.s=0
    def playTurn(self, player):
        if self.deck.getNumCards()!=0:
            if player=="hand1":
                self.player=self.hand1
            if player=="hand2":
                self.player=self.hand2
            self.s+=1
            if self.s==1:
                print("First Card in the table", str(self.y),"\n")
            if self.s!=1:
                print("Last Played Card", str(self.y),"\n")
            print("Player1:",self.hand1,)
            print("Player2:",self.hand2,"\n")
            print("Deck:", self.deck,"\n")
            l=[]
            for i in range (self.player.getNumCards()):
                self.t=self.player.getCard(i)
                if self.t.canPlay(self.y)==True:
                    l.append(self.t)
            if len(l)!=0:
                print(l[0], "Playable with", self.y,"\n")
                self.y=l[0]
                self.player.list.remove(l[0])
            self.f=self.deck.getTopCard()			
            if self.f.canPlay(self.y)==True and len(l)==0:
                self.k=self.f
                self.deck.list.remove(self.deck.getTopCard())
                print("withdrawed", self.k, "Playable with ", str(self.y),"\n")
                self.y=self.k
            if self.f.canPlay(self.y)==False and len(l)==0:
                self.player.addCard(self.deck.getTopCard())
                self.deck.list.remove(self.deck.getTopCard())
            if player=="hand1":
                print("Player1 is done. It's Player2's turn.","\n")
            if player=="hand2":
                print("Player2 is done. It's Player1's turn.","\n")
    def printResult(self):
        if self.deck.getNumCards()==0:
            if self.hand1.getNumCards()>self.hand2.getNumCards():
                return "Player2 won"
            if self.hand2.getNumCards()>self.hand1.getNumCards():
                return "Player1 won"
            if self.hand2.getNumCards()==self.hand1.getNumCards():
                return "It ended in a draw"
        if self.hand1.getNumCards()==0:
            return "Player1 won"
        if self.hand2.getNumCards()==0:
            return "Player2 won"
        if self.hand1.getNumCards()!=0 and self.hand2.getNumCards()!=0:
            return "no"
x=int(input("Number of laps:"))
P1s=0
P2s=0
for i in range(x):
	print("\n","Tour",i+1,":")
	def main():
		global my_game
		my_game = Uno()
		my_game.playGame()	
	main()
	def Continous(x):
		global P1s
		global P2s
		my_game.playTurn(x)
		if my_game.printResult()!="no":
			print(my_game.printResult())
			if my_game.printResult()=="Player1 won":
				P1s+=1
			if my_game.printResult()=="Player2 won":
				P2s+=1
		if my_game.printResult()=="no":
			if x=="hand1":
				Continous("hand2")
			if x=="hand2":
				Continous("hand1")
	Continous("hand1")
print("\n","Score Tablo: Player1  Player2")
print("              	",P1s,"      ",P2s)