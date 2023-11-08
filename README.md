# SDN Dynamic Routing - project for SDN course

In order to run mininet with topology **POLSKA**, type in:
`sudo mn --custom topo-polska.py --topo mytopo`

In order to run mininet with *floodlight* controller and topology **POLSKA**, type in:
`sudo mn --controller=remote,ip=<controller-ip>,port=6653 --custom topo-polska.py --topo mytopo
`
