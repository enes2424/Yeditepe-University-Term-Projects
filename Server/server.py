from socket import AF_INET, socket, SOCK_STREAM
from threading import Thread
from datetime import datetime
import threading
import sqlite3
import os
import random
import string

path = os.path.realpath(__file__)
path = path[:path.rfind("/") + 1]
if (len(path) == 0):
	path = os.path.realpath(__file__)
	path = path[:path.rfind("\\") + 1]

ADDRS = []
HOST = ""
PORT = 12345
BUFFERSIZE = 1024
ADDR = (HOST, PORT)
SERVER = socket(AF_INET, SOCK_STREAM)
mutex = threading.Lock()
create_table_if_not_exist = ''' CREATE TABLE IF NOT EXISTS sign (
								id INTEGER PRIMARY KEY,
								job TEXT NOT NULL,
								name TEXT NOT NULL,
								surname TEXT NOT NULL,
								email TEXT NOT NULL UNIQUE,
								password TEXT NOT NULL,
								cash INTEGER)'''
create_order_table_if_not_exist = ''' CREATE TABLE IF NOT EXISTS orders (
								id INTEGER PRIMARY KEY,
								email TEXT NOT NULL,
								x INTEGER,
								y INTEGER,
								type TEXT NOT NULL,
								company TEXT NOT NULL,
								price INTEGER,
								date DATE,
								code TEXT NOT NULL UNIQUE,
								status TEXT NOT NULL,
								driverEmail TEXT NOT NULL)'''

def	connect_client():
	while True:
		client, client_address = SERVER.accept()
		if client.recv(BUFFERSIZE).decode("utf-8") == "exit":
			return
		print("%s:%s connected." %client_address)
		ADDRS.append(client)
		Thread(target=recv_msg, args=(client, client_address)).start()

def updateStatus(client, str, code):
	mutex.acquire()
	conn = sqlite3.connect(path + 'orders.db')
	cursor = conn.cursor()
	cursor.execute(create_order_table_if_not_exist)
	cursor.execute('UPDATE orders SET status = ? WHERE code = ?;' , (str, code))
	client.sendall("succesful".encode("utf-8"))
	conn.commit()
	conn.close()
	mutex.release()

def	recv_msg(client, client_address):
	while True:
		msgs = client.recv(BUFFERSIZE).decode("utf-8").split()
		if len(msgs) == 0 or msgs[0] == "exit":
			print("%s:%s left server." %client_address)
			ADDRS.remove(client)
			client.close()
			return
		elif msgs[0] == "sign":
			mutex.acquire()
			conn = sqlite3.connect(path + 'sign.db')
			cursor = conn.cursor()
			cursor.execute(create_table_if_not_exist)
			cursor.execute('SELECT * FROM sign')
			signs = cursor.fetchall()
			if is_match(signs, msgs, 4, 3) != None:
				client.sendall("fail".encode("utf-8"))
			else:
				client.sendall(("successful " + str(len(signs) + 1)).encode("utf-8"))
				cursor.executemany('INSERT INTO sign VALUES (?, ?, ?, ?, ?, ?, ?)', [(len(signs) + 1, "customer", msgs[1], msgs[2], msgs[3], msgs[4], 0)])
			conn.commit()
			conn.close()
			mutex.release()
		elif msgs[0] == "log":
			mutex.acquire()
			conn = sqlite3.connect(path + 'sign.db')
			cursor = conn.cursor()
			cursor.execute(create_table_if_not_exist)
			cursor.execute('SELECT * FROM sign')
			signs = cursor.fetchall()
			data = is_match(signs, msgs, 4, 1)
			if data == None or data[5] != msgs[2]:
				client.sendall("fail".encode("utf-8"))
			else:
				data = list(data)
				data[0] = str(data[0])
				data[6] = str(data[6])
				client.sendall((" ".join(data)).encode("utf-8"))
			conn.commit()
			conn.close()
			mutex.release()
		elif msgs[0] == "change":
			mutex.acquire()
			conn = sqlite3.connect(path + 'sign.db')
			cursor = conn.cursor()
			cursor.execute(create_table_if_not_exist)
			cursor.execute('SELECT * FROM sign')
			signs = cursor.fetchall()
			if is_match(signs, msgs, 4, 4) != None and msgs[1] != msgs[4]:
				client.sendall("fail".encode("utf-8"))
			else:
				sign = is_match(signs, msgs, 4, 1)
				if msgs[5] == "":
					update_query = '''UPDATE sign
								SET name = ?, surname = ?, email = ?
								WHERE email = ?;'''
					cursor.execute(update_query, (msgs[2], msgs[3], msgs[4], msgs[1]))
				else:
					update_query = '''UPDATE sign
								SET name = ?, surname = ?, email = ?, password = ?
								WHERE email = ?;'''
					cursor.execute(update_query, (msgs[2], msgs[3], msgs[4], msgs[5], msgs[1]))
				if (sign[1] != "operator"):
					conn2 = sqlite3.connect(path + 'orders.db')
					cursor2 = conn2.cursor()
					cursor2.execute(create_order_table_if_not_exist)
					if (sign[1] == "customer"):
						cursor2.execute("UPDATE orders SET email = ? WHERE email = ?;", (msgs[4], msgs[1]))
					else:
						cursor2.execute("UPDATE orders SET driverEmail = ? WHERE driverEmail = ?;", (msgs[4], msgs[1]))
					conn2.commit()
					conn2.close()
				client.sendall("successful".encode("utf-8"))
			conn.commit()
			conn.close()
			mutex.release()
		elif msgs[0] == "code":
			mutex.acquire()
			conn = sqlite3.connect(path + 'orders.db')
			cursor = conn.cursor()
			cursor.execute(create_order_table_if_not_exist)
			cursor.execute('SELECT * FROM orders')
			orders = cursor.fetchall()
			order = is_match(orders, msgs, 8, 1)
			if order == None:
				client.sendall("fail".encode("utf-8"))
			else:
				order = list(order)
				order[0] = str(order[0])
				order[2] = str(order[2])
				order[3] = str(order[3])
				order[6] = str(order[6])
				client.sendall((" ".join(order)).encode("utf-8"))
			conn.commit()
			conn.close()
			mutex.release()
		elif msgs[0] == "driverOrders":
			mutex.acquire()
			conn = sqlite3.connect(path + 'orders.db')
			cursor = conn.cursor()
			cursor.execute(create_order_table_if_not_exist)
			cursor.execute('SELECT * FROM orders WHERE driverEmail = ?', (msgs[1],))
			orders = cursor.fetchall()
			if len(orders) == 0:
				client.sendall("fail".encode("utf-8"))
			else:
				order = list(orders[int(msgs[2])])
				order[0] = str(order[0])
				order[2] = str(order[2])
				order[3] = str(order[3])
				order[6] = str(order[6])
				client.sendall((str(len(orders)) + " " + " ".join(order)).encode("utf-8"))
			conn.commit()
			conn.close()
			mutex.release()
		elif msgs[0] == "cancel":
			updateStatus(client, "Queued_for_Delivery", msgs[1])
		elif msgs[0] == "drive":
			updateStatus(client, "Dispatched", msgs[1])
		elif msgs[0] == "arrived":
			updateStatus(client, "Arrived", msgs[1])
		elif msgs[0] == "attempted":
			updateStatus(client, "Stored", msgs[1])
		elif msgs[0] == "delivered":
			updateStatus(client, "Delivered", msgs[1])
		elif msgs[0] == "activeOrders":
			mutex.acquire()
			conn = sqlite3.connect(path + 'orders.db')
			cursor = conn.cursor()
			cursor.execute(create_order_table_if_not_exist)
			cursor.execute('SELECT * FROM orders WHERE email = ? and status != "Delivered"', (msgs[1],))
			orders = cursor.fetchall()
			if len(orders) == 0:
				client.sendall("fail".encode("utf-8"))
			else:
				order = list(orders[int(msgs[2])])
				order[0] = str(order[0])
				order[2] = str(order[2])
				order[3] = str(order[3])
				order[6] = str(order[6])
				client.sendall((str(len(orders)) + " " + " ".join(order)).encode("utf-8"))
			conn.commit()
			conn.close()
			mutex.release()
		elif msgs[0] == "pastOrders":
			mutex.acquire()
			conn = sqlite3.connect(path + 'orders.db')
			cursor = conn.cursor()
			cursor.execute(create_order_table_if_not_exist)
			cursor.execute('SELECT * FROM orders WHERE email = ? and status = "Delivered"', (msgs[1],))
			orders = cursor.fetchall()
			if len(orders) == 0:
				client.sendall("fail".encode("utf-8"))
			else:
				order = list(orders[int(msgs[2])])
				order[0] = str(order[0])
				order[2] = str(order[2])
				order[3] = str(order[3])
				order[6] = str(order[6])
				client.sendall((str(len(orders)) + " " + " ".join(order)).encode("utf-8"))
			conn.commit()
			conn.close()
			mutex.release()
		elif msgs[0] == "swap":
			mutex.acquire()
			conn = sqlite3.connect(path + 'sign.db')
			cursor = conn.cursor()
			cursor.execute(create_table_if_not_exist)
			cursor.execute('SELECT * FROM sign WHERE email = ? and job = "driver"', (msgs[2],))
			sign = cursor.fetchall()
			if len(sign) == 0:
				client.sendall("fail".encode("utf-8"))
			else:
				conn.commit()
				conn.close()
				conn = sqlite3.connect(path + 'orders.db')
				cursor = conn.cursor()
				cursor.execute(create_table_if_not_exist)
				cursor.execute('SELECT code FROM orders WHERE driverEmail = ? and date = ? and status == "Queued_for_Delivery"', (msgs[1], msgs[3]))
				codes = list(cursor.fetchall())
				for code in codes:
					cursor.execute('UPDATE orders SET driverEmail = ? WHERE code = ?; ', (msgs[2], code[0]))
				cursor.execute('SELECT code FROM orders WHERE driverEmail = ? and date = ? and status == "Queued_for_Delivery"', (msgs[2], msgs[3]))
				codes = list(cursor.fetchall())
				for code in codes:
					cursor.execute('UPDATE orders SET driverEmail = ? WHERE code = ?; ', (msgs[1], code[0]))
				client.sendall("succesful".encode("utf-8"))
			conn.commit()
			conn.close()
			mutex.release()

def is_match(list, msgs, index1, index2):
	for elm in list:
		if elm[index1] == msgs[index2]:
			return elm
	return None

def input_server():
	while True:
		msgs = input().split()
		if msgs[0] == "info":
			mutex.acquire()
			conn = sqlite3.connect(path + 'sign.db')
			cursor = conn.cursor()
			cursor.execute(create_table_if_not_exist)
			cursor.execute('SELECT * FROM sign')
			signs = cursor.fetchall()
			conn.commit()
			conn.close()
			for sign in signs:
				print(sign)
			mutex.release()
		elif msgs[0] == "infoOrder":
			mutex.acquire()
			conn = sqlite3.connect(path + 'orders.db')
			cursor = conn.cursor()
			cursor.execute(create_order_table_if_not_exist)
			cursor.execute('SELECT * FROM orders')
			orders = cursor.fetchall()
			conn.commit()
			conn.close()
			for order in orders:
				print(order)
			mutex.release()
		elif msgs[0] == "empty":
			mutex.acquire()
			conn = sqlite3.connect(path + 'sign.db')
			cursor = conn.cursor()
			cursor.execute(create_table_if_not_exist)
			cursor.execute('''DELETE FROM sign''')
			conn.commit()
			conn.close()
			mutex.release()
		elif msgs[0] == "add":
			if (len(msgs) != 6):
				print("missing information!!")
				continue
			elif (msgs[1] != "driver" and msgs[1] != "operator" and msgs[1] != "customer"):
				print("There is no such job!!")
				continue
			mutex.acquire()
			conn = sqlite3.connect(path + 'sign.db')
			cursor = conn.cursor()
			cursor.execute(create_table_if_not_exist)
			cursor.execute('SELECT * FROM sign')
			signs = cursor.fetchall()
			if (is_match(signs, msgs, 4, 4) != None):
				print("This email is already in use!!")
			elif len(msgs[5]) < 7:
				print("Password cannot be shorter than 7 characters")
			elif not msgs[4].endswith("@std.yeditepe.edu.tr"):
				print("Email addresses should have the extension @std.yeditepe.edu.tr")
			else:
				cursor.executemany('INSERT INTO sign VALUES (?, ?, ?, ?, ?, ?, ?)', [(len(signs) + 1, msgs[1], msgs[2], msgs[3], msgs[4], msgs[5], 0)])
			conn.commit()
			conn.close()
			mutex.release()
		elif msgs[0] == "order":
			if (len(msgs) != 9):
				print("missing information!!")
				continue
			mutex.acquire()
			conn = sqlite3.connect(path + 'sign.db')
			cursor = conn.cursor()
			cursor.execute(create_table_if_not_exist)
			cursor.execute('SELECT * FROM sign')
			signs = cursor.fetchall()
			def parse_date(date_string):
				formats = ["%Y-%m-%d", "%d-%m-%Y", "%m/%d/%Y", "%Y/%m/%d"]
				for date_format in formats:
					try:
						return datetime.strptime(date_string, date_format)
					except ValueError:
						continue
				raise ValueError("fail")
			def randomCreateCode(list):
				characters = string.ascii_letters + string.digits
				code = ''.join(random.choice(characters) for _ in range(10))
				while (is_match(list, [code], 8 ,0) != None):
					pass
				return code
			sign = is_match(signs, msgs, 4, 1)
			if sign == None or sign[1] != "customer":
				print("No customer matches this email")
			elif is_match(signs, msgs, 4, 8) == None or is_match(signs, msgs, 4, 8)[1] != "driver":
				print("No driver matches this email")
			elif not msgs[2].lstrip('-').isdigit():
				print("The x-coordinate is not in the correct format")
			elif not msgs[3].lstrip('-').isdigit():
				print("The y-coordinate is not in the correct format")
			elif not msgs[6].isdigit():
				print("The price is not in the correct format")
			else:
				try:
					date = parse_date(msgs[7])
					if (date.date() < datetime.now().date()):
						print("Orders cannot be taken for a past date :D")
					elif ((date.date() - datetime.now().date()).days > 200):
						print("Orders can be placed for up to 200 days in advance")
					else:
						conn2 = sqlite3.connect(path + 'orders.db')
						cursor2 = conn2.cursor()
						cursor2.execute(create_order_table_if_not_exist)
						cursor2.execute('SELECT * FROM orders')
						orders = cursor2.fetchall()
						cursor2.executemany('INSERT INTO orders VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)', [(len(orders) + 1, msgs[1], msgs[2], msgs[3], msgs[4], msgs[5], msgs[6], date.strftime("%Y-%m-%d"), randomCreateCode(orders), "Queued_for_Delivery", msgs[8])])
						cursor.execute("UPDATE sign SET cash = ? WHERE email = ?;", (int(sign[6]) + int(msgs[6]) // 100, msgs[1]))
						conn2.commit()
						conn2.close()
				except ValueError:
					print("The date is not in the correct format")
					print("The date must be in format %Y-%m-%d, %d-%m-%Y, %m/%d/%Y, or %Y/%m/%d")
			conn.commit()
			conn.close()
			mutex.release()
		elif msgs[0] == "exit":
			if (len(ADDRS) == 0):
				return
			print("The exit cannot be made because there are " + str(len(ADDRS)) + " clients connected to the server.")
		else:
			print("No such command exists")

if __name__ == "__main__":
	try:
		SERVER.bind(ADDR)
		conn = sqlite3.connect(path + 'sign.db')
		cursor = conn.cursor()
		cursor.execute(create_table_if_not_exist)
		conn.commit()
		conn.close()
		SERVER.listen()
		ACCEPT_THREAD = Thread(target=connect_client)
		ACCEPT_THREAD.start()
		ACCEPT_THREAD2 = Thread(target=input_server)
		ACCEPT_THREAD2.start()
		ACCEPT_THREAD2.join()
		HOST = "127.0.0.1"
		PORT = 12345
		BUFFERSIZE = 1024
		ADDR = (HOST, PORT)
		CLIENT = socket(AF_INET, SOCK_STREAM)
		CLIENT.connect(ADDR)
		CLIENT.sendall("exit".encode("utf-8"))
		ACCEPT_THREAD.join()
		SERVER.close()
	except OSError:
		print("This port is in use")
