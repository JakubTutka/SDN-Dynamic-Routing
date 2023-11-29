# SDN Dynamic Routing - project for SDN course

In order to run mininet with topology **POLSKA**, type in:

`sudo mn --custom topo-polska.py --topo mytopo`


In order to run mininet with *floodlight* controller and topology **POLSKA**, type in:

`sudo mn --controller=remote,ip=<controller-ip>,port=6653 --custom topo-polska.py --topo mytopo
`

## Generator ruchu
Zdecydowaliśmy się użyć iperf do generowania ruchu w sieci poprzez wszystkie hosty.
Chcemy całość zautomatyzować oraz zaprogramować z użyciem języka Python oraz wykorzystaniem API Minineta.
Dokumentacja [iperfa](https://iperf.fr/iperf-doc.php).

### Literatura:
1. Flow-Aware Multi-Topology Adaptive Routing - Robert Wójcik; Jerzy Domżał; Zbigniew Duliński
2. A dynamic multipath scheduling protocol (DMSP) for full performance isolation of links in software defined networking (SDN) - Syed Asad Hussain; Shuja Akbar; Imran Raza,
3. POX-PLUS: An SDN Controller with Dynamic Shortest Path Routing - Muteb Alshammari; Abdelmounaam Rezgui
