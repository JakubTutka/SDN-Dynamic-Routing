from mininet.topo import Topo
from mininet.net import Mininet
from mininet.node import CPULimitedHost
from mininet.link import TCLink
from mininet.util import dumpNodeConnections
from mininet.log import setLogLevel
from mininet.node import Controller,RemoteController

from topo_polska import MyTopo
import threading
import time
import random
import sys

def iperf_mini_generator(net ,host_start, host_end, traffic_type="UDP", traffic_bw="1M",time_s=5, port_nr=5001):
	h_start, h_end = net.get(host_start, host_end)
	net.iperf(hosts=(h_start,h_end), l4Type=traffic_type, udpBw=str(traffic_bw)+"M", seconds=time_s, port=port_nr)


def iperfTest():
        """
	Moze dorzucimy to docstringa pozniej.
	Chyba, ze nie ma tego w wymaganiach projektu.
        """
	# tworzenie obiektu topologii oraz sieci Mininet
        topo = MyTopo()
	remote_controller=RemoteController('c0',ip='127.0.0.1',port=6653)
        net = Mininet(topo=topo, link=TCLink, controller=remote_controller)
	
	# start sieci mininetowej
        net.start()
	
	#zebranie wszystkich nodow	
	nodes = net.topo.nodes()

	# lista nodow, ktore beda robic ruchy
	host_nodes = [node for node in nodes if node[0]=='h']
	
	# konczenie wszelkich wczesniejszych polaczen
        print("Dumping connections...")
        dumpNodeConnections(net.hosts)
        
	print("IPERF TRAFFIC GENERATOR...")
	
	# czas na przygotowanie iperfa
	time.sleep(5)
	
	try:
		while True:
			
			# losowanie wszelkich parametrow dla danego watku/ruchu
			pairs_nodes = host_nodes[:]

			l11 = random.choice(pairs_nodes)
			pairs_nodes.remove(l11)

                	l12 = random.choice(pairs_nodes)
               		pairs_nodes.remove(l12)
			
			time1=random.randint(1,3)
			bw1=round(random.uniform(0.1,1.0),1)

                	l21 = random.choice(pairs_nodes)
                	pairs_nodes.remove(l21)

                	l22 = random.choice(pairs_nodes)
                	pairs_nodes.remove(l22)
			
			time2=random.randint(1,3)
			bw2=round(random.uniform(0.1,1.0),1)

               		l31 = random.choice(pairs_nodes)
                	pairs_nodes.remove(l31)

                	l32 = random.choice(pairs_nodes)
                	pairs_nodes.remove(l32)
			
			time3=random.randint(1,3)
			bw3=round(random.uniform(0.1,1.0),1)
			
                	l41 = random.choice(pairs_nodes)
                	pairs_nodes.remove(l41)

                	l42 = random.choice(pairs_nodes)
                	pairs_nodes.remove(l42)

			time4=random.randint(1,3)
			bw4=round(random.uniform(0.1,1.0),1)			

                	l51 = random.choice(pairs_nodes)
                	pairs_nodes.remove(l51)

                	l52 = random.choice(pairs_nodes)
                	pairs_nodes.remove(l52)
			
			time5=random.randint(1,3)	
			bw5=round(random.uniform(0.1,1.0),1)			

                	l61 = random.choice(pairs_nodes)
                	pairs_nodes.remove(l61)

                	l62 = random.choice(pairs_nodes)
                	pairs_nodes.remove(l62)
			
			time6=random.randint(1,7)
			bw6=round(random.uniform(0.1,1.0),1)			

			t1 = threading.Thread(target=iperf_mini_generator, args=(net,l11,l12,"UDP",bw1,time1,5001))
			t2 = threading.Thread(target=iperf_mini_generator, args=(net,l21,l22,"UDP",bw2,time2,5002))
			t3 = threading.Thread(target=iperf_mini_generator, args=(net,l31,l32,"UDP",bw3,time3,5003))
			t4 = threading.Thread(target=iperf_mini_generator, args=(net,l41,l42,"UDP",bw4,time4,5004))
			t5 = threading.Thread(target=iperf_mini_generator, args=(net,l51,l52,"UDP",bw5,time5,5005))
			t6 = threading.Thread(target=iperf_mini_generator, args=(net,l61,l62,"UDP",bw6,time6,5006))
	
			t1.start()
			t2.start()
			t3.start()
			t4.start()
			t5.start()
			t6.start()

			t1.join()
			t2.join()
			t3.join()
			t4.join()
			t5.join()
			t6.join()
			
			print("CZEKANIE 1...")
			time.sleep(10)
			dumpNodeConnections(net.hosts)
			print("CZEKANIE 2...")
			time.sleep(10)
	except KeyboardInterrupt:
		# usuwanie linkow, wezlow itp
		net.stop()
		
		print("KONIEC SYMULACJI RUCHU!")
		sys.exit()
#setLogLevel('info')
iperfTest()
