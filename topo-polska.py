from mininet.topo import Topo

class MyTopo( Topo ):
	"POLSKA - topology with 12 nodes - SDN project"

	def __init__( self ): 

		# Initialize topology
		Topo.__init__( self )

		# Add hosts and switches
		szczecin  = self.addSwitch('szczecin',  dpid='00:00:00:00:00:00:01:00')
		kolobrzeg = self.addSwitch('kolobrzeg', dpid='00:00:00:00:00:00:02:00')
		gdansk    = self.addSwitch('gdansk',    dpid='00:00:00:00:00:00:03:00')
		bialystok = self.addSwitch('bialystok', dpid='00:00:00:00:00:00:04:00')
		poznan    = self.addSwitch('poznan',    dpid='00:00:00:00:00:00:05:00')
		bydgoszcz = self.addSwitch('bydgoszcz', dpid='00:00:00:00:00:00:06:00')
		warszawa  = self.addSwitch('warszawa',  dpid='00:00:00:00:00:00:07:00')
		wroclaw   = self.addSwitch('wroclaw',   dpid='00:00:00:00:00:00:08:00')
		lodz      = self.addSwitch('lodz',      dpid='00:00:00:00:00:00:09:00')
		katowice  = self.addSwitch('katowice',  dpid='00:00:00:00:00:00:10:00')
		krakow    = self.addSwitch('krakow',    dpid='00:00:00:00:00:00:11:00')
		rzeszow   = self.addSwitch('rzeszow',   dpid='00:00:00:00:00:00:12:00')

		# host = self.addHost( 'host' )
	
		# Add links
		self.addLink( szczecin, kolobrzeg )
		self.addLink( szczecin, poznan )

		self.addLink( kolobrzeg, bydgoszcz )
		self.addLink( kolobrzeg, gdansk )

		self.addLink( gdansk, warszawa )
		self.addLink( gdansk, bialystok )

		self.addLink( bialystok, warszawa )
		self.addLink( bialystok, rzeszow )

		self.addLink( poznan, bydgoszcz )
		self.addLink( poznan, wroclaw )

		self.addLink( bydgoszcz, warszawa )

		self.addLink( warszawa, lodz )
		self.addLink( warszawa, krakow )

		self.addLink( wroclaw, lodz )
		self.addLink( wroclaw, katowice )

		self.addLink( lodz, katowice )

		self.addLink( katowice, krakow )

		self.addLink( krakow, rzeszow )

topos = { 'mytopo': ( lambda: MyTopo() ) }