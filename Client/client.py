from socket import AF_INET, socket, SOCK_STREAM
from threading import Thread
from tkinter import messagebox
from datetime import datetime
from geneticAlgorithm.genetic_algorithm import genetic_algorithm
import os
import tkinter as tk

path = os.path.realpath(__file__)
path = path[:path.rfind("/") + 1]
if (len(path) == 0):
	path = os.path.realpath(__file__)
	path = path[:path.rfind("\\") + 1]

HOST = "127.0.0.1"
PORT = 12345
BUFFERSIZE = 1024
ADDR = (HOST, PORT)
CLIENT = socket(AF_INET, SOCK_STREAM)

def redraw_window(num=0):
	widget_list = window.winfo_children()
	for widget in widget_list:
		widget.destroy()
	tk.Label(window, text="ELLIE'S", bg="lightgreen", font=("Helvetica", 50), width=20, height=3).place(x=-100, y=0)
	img = tk.PhotoImage(file=path + "dellies.png")
	label = tk.Label(window, bg="lightgreen", image=img)
	label.image=img
	label.place(x=40, y=20)
	if (num == 0):
		menubar = tk.Menu(window)
		help_menu = tk.Menu(menubar, tearoff=0)
		help_menu.add_command(label="About", command=lambda: os.system('start CSE344DesignReport-3.pdf'))
		menubar.add_cascade(label="Help", menu=help_menu)
		window.config(menu=menubar)
	window.config(bg="lightgreen")

def icon():
	tk.Label(window, text="ELLIE'S", bg="lightgreen", font=("Helvetica", 50), width=20, height=3).place(x=-100, y=0)
	img = tk.PhotoImage(file=path + "dellies.png")
	label = tk.Label(window, bg="lightgreen", image=img)
	label.image=img
	label.place(x=40, y=20)

def CargoCode(code):
	if (code != ""):
		CLIENT.sendall(("code " + code).encode("utf-8"))
		msgs = CLIENT.recv(BUFFERSIZE).decode("utf-8").split()
		if msgs[0] == "fail":
			tk.Label(window, text="The entered code does not match any cargo code", bg="lightgreen", font=("Helvetica", 11), width=40, fg="red",height=3).place(x=63, y=540)
		else:
			redraw_window()
			tk.Label(window, text=f"Customer: {msgs[1]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=180)
			tk.Label(window, text=f"Coordinates: x: {msgs[2]}, y: {msgs[3]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=220)
			tk.Label(window, text=f"Type: {msgs[4]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=260)
			tk.Label(window, text=f"Company: {msgs[5]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=300)
			tk.Label(window, text=f"Price: {msgs[6]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=340)
			tk.Label(window, text=f"Date: {msgs[7]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=380)
			if (msgs[9] == "Queued_for_Delivery"):
				tk.Label(window, text=f"Status: Your package is getting ready.", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=420)
			elif (msgs[9] == "Dispatched"):
				tk.Label(window, text=f"Status: Your package is on the way.", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=420)
			elif (msgs[9] == "Arrived"):
				tk.Label(window, text=f"Status: Your package has arrived.", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=420)
			elif (msgs[9] == "Stored"):
				tk.Label(window, text=f"Status: Delivery attempted, recipient was not available.", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=420)
			elif (msgs[9] == "Delivered"):
				tk.Label(window, text=f"Status: Your package has been delivered.", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=420)
			tk.Label(window, text=f"Driver: {msgs[10]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=460)
			tk.Button(window, text="Back", bg="green", font=("Helvetica", 20), command=start_window, width=7, height=1).place(x=185, y=510)

def start_window():
	redraw_window()
	tk.Button(window, text="Log In", bg="green", font=("Helvetica", 20), command=log_in, width=20, height=3).place(x=85, y=170)
	tk.Button(window, text="Sign Up", bg="green", font=("Helvetica", 20), command=sign_up, width=20, height=3).place(x=85, y=320)
	tk.Label(window, text="Enter cargo code", bg="lightgreen", font=("Helvetica", 20), width=26).place(x=40, y=450)
	code = tk.Entry(window, font=("Helvetica", 28), width=13)
	code.place(x=85, y=490)
	img = tk.PhotoImage(file=path + "handglass.png")
	button = tk.Button(window, image=img, command=lambda: CargoCode(code.get()))
	button.image = img
	button.place(x=365, y=490)

def loading_window(num):
	redraw_window(1)
	img = tk.PhotoImage(file=path + "loading.png")
	label = tk.Label(window, bg="lightgreen", image=img)
	label.image=img
	label.place(x=27, y=291)
	img = tk.PhotoImage(file=path + "piece.png")
	for i in range (num):
		label = tk.Label(window, bg="black", image=img)
		label.image=img
		label.place(x=40 + 11 * i, y=300)
	if num < 38:
		window.after(50, loading_window, num + 1)
	else:
		window.after(1000, start_window)

def setting(user):
	redraw_window()
	tk.Label(window, text="Name", bg="lightgreen", font=("Helvetica", 15), width=5).place(x=0, y=190)
	name = tk.Entry(window, font=("Helvetica", 15), width=28)
	name.place(x=170, y=190)
	name.insert(0, user.name)
	tk.Label(window, text="Surname", bg="lightgreen", font=("Helvetica", 15), width=8).place(x=0, y=240)
	surname = tk.Entry(window, font=("Helvetica", 15), width=28)
	surname.place(x=170, y=240)
	surname.insert(0, user.surname)
	tk.Label(window, text="Email", bg="lightgreen", font=("Helvetica", 15), width=5).place(x=0, y=290)
	email = tk.Entry(window, font=("Helvetica", 15), width=28)
	email.place(x=170, y=290)
	email.insert(0, user.email)
	tk.Label(window, text="Current Password", bg="lightgreen", font=("Helvetica", 15), width=15).place(x=0, y=340)
	password = tk.Entry(window, show="*", font=("Helvetica", 15), width=28)
	password.place(x=170, y=340)
	tk.Label(window, text="New Password", bg="lightgreen", font=("Helvetica", 15), width=13).place(x=0,y=390)
	newPassword = tk.Entry(window, show="*", font=("Helvetica", 15), width=28)
	newPassword.place(x=170, y=390)
	errorlabel = tk.Label(window, text="", bg="lightgreen", font=("Helvetica", 15), width=35, fg="red",height=3)
	errorlabel.place(x=70, y=520)
	def setting2():
		if name.get() == "":
			errorlabel.config(text="Name field cannot be empty", fg="red")
			return
		elif surname.get() == "":
			errorlabel.config(text="Surname field cannot be empty", fg="red")
			return
		elif email.get() == "":
			errorlabel.config(text="Email field cannot be empty", fg="red")
			return
		elif password.get() == "":
			errorlabel.config(text="Password field cannot be empty", fg="red")
			return
		elif password.get() != user.password:
			errorlabel.config(text="Current password was entered incorrectly", fg="red")
			return
		user.name = name.get()
		user.surname = surname.get()
		if newPassword.get() != "":
			user.password = newPassword.get()
		CLIENT.sendall(("change " + user.email + " " + user.name + " " + user.surname + " " + email.get() + " " + user.password).encode("utf-8"))
		user.email = email.get()
		msgs = CLIENT.recv(BUFFERSIZE).decode("utf-8").split()
		if msgs[0] == "successful":
			errorlabel.config(text="Information update was successful", fg="green")
		elif msgs[0] == "fail":
			errorlabel.config(text="This email is already in use", fg="red")
	tk.Button(window, text="Change Informations", bg="green", font=("Helvetica", 20), command=setting2,width=20, height=2).place(x=150, y=440)
	tk.Button(window, text="Back", bg="green", font=("Helvetica", 20), command=user.screen, width=5,height=2).place(x=30, y=440)

class User:
	def __init__(self, id, name, surname, email, password):
		self.id = int(id)
		self.name = name
		self.surname = surname
		self.email = email
		self.password = password

class Customer(User):
	def __init__(self, id, name, surname, email, password, DelliePoint):
		super().__init__(id, name, surname, email, password)
		self.DelliePoint = int(DelliePoint)

	def delivered(self, code):
		CLIENT.sendall(("delivered " + code).encode("utf-8"))
		CLIENT.recv(BUFFERSIZE)
		self.screen()

	def activeOrders(self, num):
		CLIENT.sendall(("activeOrders " + self.email + " " + str(num)).encode("utf-8"))
		msgs = CLIENT.recv(BUFFERSIZE).decode("utf-8").split()
		redraw_window()
		if msgs[0] != "fail":
			tk.Label(window, text=f"Driver: {msgs[11]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=170)
			tk.Label(window, text=f"Coordinates: x: {msgs[3]}, y: {msgs[4]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=200)
			tk.Label(window, text=f"Type: {msgs[5]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=230)
			tk.Label(window, text=f"Company: {msgs[6]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=260)
			tk.Label(window, text=f"Price: {msgs[7]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=290)
			tk.Label(window, text=f"Date: {msgs[8]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=320)
			if (msgs[10] == "Queued_for_Delivery"):
				tk.Label(window, text=f"Status: Your package is getting ready.", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=350)
			elif (msgs[10] == "Dispatched"):
				tk.Label(window, text=f"Status: Your package is on the way.", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=350)
			elif (msgs[10] == "Arrived"):
				tk.Label(window, text=f"Status: Your package has arrived.", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=350)
			elif (msgs[10] == "Stored"):
				tk.Label(window, text=f"Status: Delivery attempted, recipient was not available.", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=350)
			elif (msgs[10] == "Delivered"):
				tk.Label(window, text=f"Status: Your package has been delivered.", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=350)
			tk.Label(window, text=f"Code: {msgs[9]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=380)
			if (int(msgs[0]) > num + 1):
				tk.Button(window, text="Next", bg="green", font=("Helvetica", 20), command=lambda: self.activeOrders(num + 1), width=7, height=1).place(x=350, y=510)
			if (num != 0):
				tk.Button(window, text="Prev", bg="green", font=("Helvetica", 20), command=lambda: self.activeOrders(num - 1), width=7, height=1).place(x=20, y=510)
			if (msgs[10] == "Arrived"):
				tk.Button(window, text="Delivered", bg="green", font=("Helvetica", 20), command=lambda: self.delivered(msgs[9]), width=7, height=1).place(x=185, y=420)
		tk.Button(window, text="Back", bg="green", font=("Helvetica", 20), command=self.screen, width=7, height=1).place(x=185, y=510)

	def pastOrders(self, num):
		CLIENT.sendall(("pastOrders " + self.email + " " + str(num)).encode("utf-8"))
		msgs = CLIENT.recv(BUFFERSIZE).decode("utf-8").split()
		redraw_window()
		if msgs[0] != "fail":
			tk.Label(window, text=f"Driver: {msgs[11]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=170)
			tk.Label(window, text=f"Coordinates: x: {msgs[3]}, y: {msgs[4]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=200)
			tk.Label(window, text=f"Type: {msgs[5]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=230)
			tk.Label(window, text=f"Company: {msgs[6]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=260)
			tk.Label(window, text=f"Price: {msgs[7]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=290)
			tk.Label(window, text=f"Date: {msgs[8]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=320)
			tk.Label(window, text="Status: Your package has been delivered.", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=350)
			tk.Label(window, text=f"Code: {msgs[9]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=380)
			if (int(msgs[0]) > num + 1):
				tk.Button(window, text="Next", bg="green", font=("Helvetica", 20), command=lambda: self.pastOrders(num + 1), width=7, height=1).place(x=350, y=510)
			if (num != 0):
				tk.Button(window, text="Prev", bg="green", font=("Helvetica", 20), command=lambda: self.pastOrders(num - 1), width=7, height=1).place(x=20, y=510)
		tk.Button(window, text="Back", bg="green", font=("Helvetica", 20), command=self.screen, width=7, height=1).place(x=185, y=510)

	def screen(self):
		redraw_window()
		img = tk.PhotoImage(file=path + "setting.png")
		button = tk.Button(window, bg="lightgreen", command=lambda: setting(self), image=img)
		button.image=img
		button.place(x=390, y=180)
		tk.Label(window, text="Logged in as: ", bg="lightgreen", fg="green", font=("Helvetica", 20), width=15, height = 2).place(x=0, y=150)
		tk.Label(window, text="Customer", bg="lightgreen", font=("Helvetica", 20), width=8, height = 2).place(x=200, y=150)
		tk.Label(window, text="Points: ", bg="lightgreen", fg="green", font=("Helvetica", 20), width=10, height = 2).place(x=0, y=200)
		tk.Label(window, text=str(self.DelliePoint), bg="lightgreen", font=("Helvetica", 20), width=len(str(self.DelliePoint)), height = 2).place(x=150, y=200)
		tk.Button(window, text="Active Orders", bg="green", font=("Helvetica", 20), command=lambda: self.activeOrders(0), width=20, height=2).place(x=80, y=280)
		tk.Button(window, text="Past Orders", bg="green", font=("Helvetica", 20), command=lambda: self.pastOrders(0), width=20, height=2).place(x=80, y=400)
		tk.Button(window, text="Back", bg="green", font=("Helvetica", 20), command=start_window, width=7, height=1).place(x=185, y=510)

class Staff(User):
	def __init__(self, id, name, surname, email, password, cash):
		super().__init__(id, name, surname, email, password)
		self.cash = int(cash)

class Driver(Staff):
	def __init__(self, id, name, surname, email, password, cash):
		super().__init__(id, name, surname, email, password, cash)

	def changeStatus(self, str, code, num):
		CLIENT.sendall((str + code).encode("utf-8"))
		CLIENT.recv(BUFFERSIZE)
		self.driverOrders(num)

	def driverOrders(self, num):
		CLIENT.sendall(("driverOrders " + self.email + " " + str(num)).encode("utf-8"))
		msgs = CLIENT.recv(BUFFERSIZE).decode("utf-8").split()
		redraw_window()
		if msgs[0] != "fail":
			tk.Label(window, text=f"Customer: {msgs[2]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=170)
			tk.Label(window, text=f"Coordinates: x: {msgs[3]}, y: {msgs[4]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=200)
			tk.Label(window, text=f"Type: {msgs[5]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=230)
			tk.Label(window, text=f"Company: {msgs[6]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=260)
			tk.Label(window, text=f"Price: {msgs[7]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=290)
			tk.Label(window, text=f"Date: {msgs[8]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=320)
			if (msgs[10] == "Queued_for_Delivery"):
				tk.Label(window, text=f"Status: Your package is getting ready.", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=350)
			elif (msgs[10] == "Dispatched"):
				tk.Label(window, text=f"Status: Your package is on the way.", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=350)
			elif (msgs[10] == "Arrived"):
				tk.Label(window, text=f"Status: Your package has arrived.", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=350)
			elif (msgs[10] == "Stored"):
				tk.Label(window, text=f"Status: Delivery attempted, recipient was not available.", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=350)
			elif (msgs[10] == "Delivered"):
				tk.Label(window, text=f"Status: Your package has been delivered.", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=350)
			tk.Label(window, text=f"Code: {msgs[9]}", bg="lightgreen", font=("Helvetica", 15)).place(x=18, y=380)
			if (int(msgs[0]) > num + 1):
				tk.Button(window, text="Next", bg="green", font=("Helvetica", 20), command=lambda: self.driverOrders(num + 1), width=7, height=1).place(x=350, y=510)
			if (num != 0):
				tk.Button(window, text="Prev", bg="green", font=("Helvetica", 20), command=lambda: self.driverOrders(num - 1), width=7, height=1).place(x=20, y=510)
			if (msgs[10] == "Queued_for_Delivery"):
				tk.Button(window, text="Drive", bg="green", font=("Helvetica", 20), command=lambda: self.changeStatus("drive ", msgs[9], num), width=7, height=1).place(x=185, y=420)
			elif (msgs[10] == "Dispatched"):
				tk.Button(window, text="Arrived", bg="green", font=("Helvetica", 20), command=lambda: self.changeStatus("arrived ", msgs[9], num), width=7, height=1).place(x=185, y=420)
				tk.Button(window, text="Cancel", bg="green", font=("Helvetica", 20), command=lambda: self.changeStatus("cancel ", msgs[9], num), width=7, height=1).place(x=40, y=420)
			elif (msgs[10] == "Arrived"):
				tk.Button(window, text="Attempted", bg="green", font=("Helvetica", 20), command=lambda: self.changeStatus("attempted ", msgs[9], num), width=9, height=1).place(x=170, y=420)
				tk.Button(window, text="Cancel", bg="green", font=("Helvetica", 20), command=lambda: self.changeStatus("cancel ", msgs[9], num), width=7, height=1).place(x=40, y=420)
			elif (msgs[10] == "Stored"):
				tk.Button(window, text="Cancel", bg="green", font=("Helvetica", 20), command=lambda: self.changeStatus("cancel ", msgs[9], num), width=7, height=1).place(x=185, y=420)
		tk.Button(window, text="Back", bg="green", font=("Helvetica", 20), command=self.screen, width=7, height=1).place(x=185, y=510)

	def swapScheduler(self):
		redraw_window()
		tk.Label(window, text="Email", bg="lightgreen", font=("Helvetica", 20), width=8).place(x=0, y=240)
		email = tk.Entry(window, font=("Helvetica", 20), width=22)
		email.place(x=150, y=240)
		tk.Label(window, text="Date", bg="lightgreen", font=("Helvetica", 20), width=8).place(x=0, y=340)
		date = tk.Entry(window, font=("Helvetica", 20), width=22)
		date.place(x=150, y=340)
		errorlabel = tk.Label(window, text="", bg="lightgreen", font=("Helvetica", 10), fg="red", height=3)
		errorlabel.place(x=10, y=530)
		def parse_date(date_string):
			formats = ["%Y-%m-%d", "%d-%m-%Y", "%m/%d/%Y", "%Y/%m/%d"]
			for date_format in formats:
				try:
					return datetime.strptime(date_string, date_format)
				except ValueError:
					continue
			raise ValueError("fail")
		def swapScheduler2():
			if email.get() == "":
				errorlabel.config(text="Email field cannot be empty")
				return
			elif date.get() == "":
				errorlabel.config(text="Date field cannot be empty")
				return
			elif email.get() == self.email:
				errorlabel.config(text="It is meaningless to swap the schedule with yourself :D")
				return
			try:
				CLIENT.sendall(("swap " + self.email + " " + email.get() + " " + parse_date(date.get()).strftime("%Y-%m-%d")).encode("utf-8"))
				msgs = CLIENT.recv(BUFFERSIZE).decode("utf-8").split()
				if msgs[0] == "fail":
					errorlabel.config(text="No driver has such an email")
				else:
					self.screen()
			except ValueError:
				errorlabel.config(text="The date must be in format %Y-%m-%d, %d-%m-%Y, %m/%d/%Y, or %Y/%m/%d")
		tk.Button(window, text="Swap Scheduler", bg="green", font=("Helvetica", 20), command=swapScheduler2, width=20, height=2).place(x=150, y=440)
		tk.Button(window, text="Back", bg="green", font=("Helvetica", 20), command=self.screen, width=5, height=2).place(x=30, y=440)

	def screen(self):
		redraw_window()
		img = tk.PhotoImage(file=path + "setting.png")
		button = tk.Button(window, bg="lightgreen", command=lambda: setting(self), image=img)
		button.image=img
		button.place(x=390, y=180)
		tk.Label(window, text="Logged in as: ", bg="lightgreen", fg="green", font=("Helvetica", 20), width=15, height = 2).place(x=0, y=150)
		tk.Label(window, text="Driver", bg="lightgreen", font=("Helvetica", 20), width=8, height = 2).place(x=200, y=150)
		tk.Label(window, text="Cash: ", bg="lightgreen", fg="green", font=("Helvetica", 20), width=9, height = 2).place(x=0, y=200)
		tk.Label(window, text=str(self.cash), bg="lightgreen", font=("Helvetica", 20), width=len(str(self.cash)), height = 2).place(x=150, y=200)
		tk.Button(window, text="Back", bg="green", font=("Helvetica", 20), command=start_window, width=7, height=1).place(x=185, y=510)
		tk.Button(window, text="Orders", bg="green", font=("Helvetica", 20), command=lambda: self.driverOrders(0), width=20, height=1).place(x=85, y=260)
		tk.Button(window, text="Find Route", bg="green", font=("Helvetica", 20), command=findRoute, width=20, height=1).place(x=85, y=320)
		tk.Button(window, text="Swap Schedule", bg="green", font=("Helvetica", 20), command=self.swapScheduler, width=20, height=1).place(x=85, y=380)
		tk.Button(window, text="Reviews", bg="green", font=("Helvetica", 20), command=unnecessary, width=20, height=1).place(x=85, y=440)

class Operator(Staff):
	def __init__(self, id, name, surname, email, password, cash):
		super().__init__(id, name, surname, email, password, cash)
	def screen(self):
		redraw_window()
		img = tk.PhotoImage(file=path + "setting.png")
		button = tk.Button(window, bg="lightgreen", command=lambda: setting(self), image=img)
		button.image=img
		button.place(x=390, y=180)
		tk.Label(window, text="Logged in as: ", bg="lightgreen", fg="green", font=("Helvetica", 20), width=15, height = 2).place(x=0, y=150)
		tk.Label(window, text="Operator", bg="lightgreen", font=("Helvetica", 20), width=8, height = 2).place(x=200, y=150)
		tk.Label(window, text="Cash: ", bg="lightgreen", fg="green", font=("Helvetica", 20), width=9, height = 2).place(x=0, y=200)
		tk.Label(window, text=str(self.cash), bg="lightgreen", font=("Helvetica", 20), width=len(str(self.cash)), height = 2).place(x=150, y=200)
		tk.Button(window, text="Chats", bg="green", font=("Helvetica", 20), command=unnecessary, width=20, height=2).place(x=80, y=280)
		tk.Button(window, text="Reviews", bg="green", font=("Helvetica", 20), command=unnecessary, width=20, height=2).place(x=80, y=400)
		tk.Button(window, text="Back", bg="green", font=("Helvetica", 20), command=start_window, width=7, height=1).place(x=185, y=510)

def unnecessary():
	pass

def showInfo():
	messagebox.showinfo(title="best path", message=str(genetic_algorithm()))
def findRoute():
	Thread(target=showInfo).start()

def log_in():
	redraw_window()
	tk.Label(window, text="Email", bg="lightgreen", font=("Helvetica", 20), width=8).place(x=0, y=240)
	email = tk.Entry(window, font=("Helvetica", 20), width=22)
	email.place(x=150, y=240)
	tk.Label(window, text="Password", bg="lightgreen", font=("Helvetica", 20), width=8).place(x=0, y=340)
	password = tk.Entry(window, show="*", font=("Helvetica", 20), width=22)
	password.place(x=150, y=340)
	errorlabel = tk.Label(window, text="", bg="lightgreen", font=("Helvetica", 15), fg="red", height=3)
	errorlabel.place(x=180, y=520)
	def log_in2():
		if email.get() == "":
			errorlabel.config(text="Email field cannot be empty")
			return
		elif password.get() == "":
			errorlabel.config(text="Password field cannot be empty")
			return
		CLIENT.sendall(("log " + email.get() + " " + password.get()).encode("utf-8"))
		msgs = CLIENT.recv(BUFFERSIZE).decode("utf-8").split()
		if msgs[0] == "fail":
			errorlabel.config(text="Email or password is wrong")
		elif msgs[1] == "customer":
			Customer(msgs[0], msgs[2], msgs[3], msgs[4], msgs[5], msgs[6]).screen()
		elif msgs[1] == "driver":
			Driver(msgs[0], msgs[2], msgs[3], msgs[4], msgs[5], msgs[6]).screen()
		elif msgs[1] == "operator":
			Operator(msgs[0], msgs[2], msgs[3], msgs[4], msgs[5], msgs[6]).screen()
	tk.Button(window, text="Log in", bg="green", font=("Helvetica", 20), command=log_in2, width=20, height=2).place(x=150, y=440)
	tk.Button(window, text="Back", bg="green", font=("Helvetica", 20), command=start_window, width=5, height=2).place(x=30, y=440)

def sign_up():
	redraw_window()
	tk.Label(window, text="Name", bg="lightgreen", font=("Helvetica", 20), width=8).place(x=0, y=200)
	name = tk.Entry(window, font=("Helvetica", 20), width=22)
	name.place(x=150, y=200)
	tk.Label(window, text="Surname", bg="lightgreen", font=("Helvetica", 20), width=8).place(x=0, y=260)
	surname = tk.Entry(window, font=("Helvetica", 20), width=22)
	surname.place(x=150, y=260)
	tk.Label(window, text="Email", bg="lightgreen", font=("Helvetica", 20), width=8).place(x=0, y=320)
	email = tk.Entry(window, font=("Helvetica", 20), width=22)
	email.place(x=150, y=320)
	tk.Label(window, text="Password", bg="lightgreen", font=("Helvetica", 20), width=8).place(x=0, y=380)
	password = tk.Entry(window, show="*", font=("Helvetica", 20), width=22)
	password.place(x=150, y=380)
	errorlabel = tk.Label(window, text="", bg="lightgreen", font=("Helvetica", 10), fg="red", height=3)
	errorlabel.place(x=80, y=520)
	def sign_up2():
		if name.get() == "":
			errorlabel.config(text="Name field cannot be empty")
			return
		elif surname.get() == "":
			errorlabel.config(text="Surname field cannot be empty")
			return
		elif email.get() == "":
			errorlabel.config(text="Email field cannot be empty")
			return
		elif password.get() == "":
			errorlabel.config(text="Password field cannot be empty")
			return
		elif len(password.get()) < 7:
			errorlabel.config(text="Password cannot be shorter than 7 characters")
			return
		elif not email.get().endswith("@std.yeditepe.edu.tr"):
			errorlabel.config(text="Email addresses should have the extension @std.yeditepe.edu.tr")
			return
		CLIENT.sendall(("sign " + name.get() + " " + surname.get() + " " + email.get() + " " + password.get()).encode("utf-8"))
		msgs = CLIENT.recv(BUFFERSIZE).decode("utf-8").split()
		if msgs[0] == "successful":
			Customer(msgs[1], name.get(), surname.get(), email.get(), password.get(), 0).screen()
		elif msgs[0] == "fail":
			errorlabel.config(text="This email is already in use")
	tk.Button(window, text="Sign up", bg="green", font=("Helvetica", 20), command=sign_up2, width=20, height=2).place(x=150, y=440)
	tk.Button(window, text="Back", bg="green", font=("Helvetica", 20), command=start_window, width=5, height=2).place(x=30, y=440)

if __name__ == "__main__":
	try:
		CLIENT.connect(ADDR)
		CLIENT.sendall("connect".encode("utf-8"))
		window = tk.Tk()
		window.title("DELLIE'S")
		window.geometry("500x600+750+200")
		window.resizable(False, False)
		loading_window(0)
		window.mainloop()
		CLIENT.sendall("exit".encode("utf-8"))
		CLIENT.close()
	except ConnectionRefusedError:
		print("Could not connect to the server")
