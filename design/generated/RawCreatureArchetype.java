package generated;

import static squidpony.squidmath.OrderedMap.makeMap;

import java.io.Serializable;
import java.util.Map;

public class RawCreatureArchetype implements Serializable {
  public static final long serialVersionUID = 1L;

  public static final RawCreatureArchetype[] ENTRIES = new RawCreatureArchetype[] {
    new RawCreatureArchetype("piranha", "AQUATIC", new String[] {"1 RIPPING BITE"}, new String[] {"Sea Skirmisher", "Pack Hunter", "Jungle Stalker"}, 1388213467227414632L),
    new RawCreatureArchetype("lobster", "AQUATIC", new String[] {"2 CRUSHING CLAW"}, new String[] {"Wave Knight", "Bug Trooper", "Deep Thinker"}, -8465949392312559882L),
    new RawCreatureArchetype("great white shark", "AQUATIC", new String[] {"1 GRABBING BITE"}, new String[] {"Sea Skirmisher", "Primeval Lord", "Wild Seeker"}, 4628606660399865997L),
    new RawCreatureArchetype("blue whale", "AQUATIC", new String[] {"1 CRUSHING SLAM"}, new String[] {"Wave Knight", "Herd Protector", "Deep Thinker"}, 9165120973054645634L),
    new RawCreatureArchetype("electric eel", "AQUATIC", new String[] {"1 SHOCKING BURST", "1 RIPPING BITE"}, new String[] {"Sea Skirmisher", "Thunder Chief", "Flowing Form"}, 8596105284413500351L),
    new RawCreatureArchetype("kraken", "AQUATIC", new String[] {"8 GRABBING SLAM", "1 GOUGING PECK"}, new String[] {"Deep Thinker", "Moist Mangler", "Wave Knight"}, -106824077097418356L),
    new RawCreatureArchetype("sea tiger", "AQUATIC", new String[] {"1 RIPPING BITE", "2 THRASHING WRAP"}, new String[] {"Wave Knight", "Feline Hunter", "Sea Skirmisher"}, -4082484486495205821L),
    new RawCreatureArchetype("platypus", "AMPHIBIOUS", new String[] {"1 CRUSHING PECK", "1 PIERCING SPUR"}, new String[] {"Deadly Venom", "Moist Mangler", "Brain Bender"}, 8738593466559868748L),
    new RawCreatureArchetype("frog", "AMPHIBIOUS", new String[] {"1 GRABBING TONGUE", "1 DISGUSTING BITE"}, new String[] {"Nasty Bandit", "Moist Mangler", "Great Grappler"}, 3161990610426862951L),
    new RawCreatureArchetype("penguin", "AMPHIBIOUS", new String[] {"1 GOUGING PECK"}, new String[] {"Sea Skirmisher", "Avian Overlord", "Ice Shaman"}, 1457682110389047008L),
    new RawCreatureArchetype("pelican", "FLYING", new String[] {"1 GRABBING PECK"}, new String[] {"Wave Knight", "Avian Overlord", "Sky Soldier"}, -637211763186975493L),
    new RawCreatureArchetype("puffin", "FLYING", new String[] {"1 CRUSHING PECK"}, new String[] {"Sky Soldier", "Winged Wrath", "Ice Shaman"}, -6450790839276127647L),
    new RawCreatureArchetype("golden eagle", "FLYING", new String[] {"1 GOUGING PECK", "1 CRUSHING CLAW"}, new String[] {"Sky Soldier", "Avian Overlord", "Winged Wrath"}, -8265858757686510940L),
    new RawCreatureArchetype("ostrich", "WALKING", new String[] {"1 GOUGING PECK", "1 CRUSHING KICK"}, new String[] {"Ground Pounder", "Avian Overlord", "Plains Champ"}, 2498094503942262959L),
    new RawCreatureArchetype("leopard", "WALKING", new String[] {"2 RIPPING CLAW", "1 RIPPING BITE"}, new String[] {"Jungle Stalker", "Feline Hunter", "Plains Champ"}, -3542488999351784073L),
    new RawCreatureArchetype("lion", "WALKING", new String[] {"2 RIPPING CLAW", "1 CRUSHING BITE"}, new String[] {"Barbaric Roar", "Pack Hunter", "Plains Champ"}, 631765795199294250L),
    new RawCreatureArchetype("barn cat", "WALKING", new String[] {"2 GRABBING CLAW", "1 RIPPING BITE"}, new String[] {"Feline Hunter", "Wild Seeker", "Tough Fluff"}, 6889477834179993741L),
    new RawCreatureArchetype("bone devil", "WALKING", new String[] {"4 PIERCING SPUR", "2 RIPPING CLAW"}, new String[] {"Fierce Frenzy", "Fiendish Fury", "Hard Body"}, 3721226321482223858L),
    new RawCreatureArchetype("ice devil", "WALKING", new String[] {"2 FREEZING SPUR", "2 GOUGING CLAW"}, new String[] {"Fierce Frenzy", "Fiendish Fury", "Ice Shaman"}, -7456705261647852789L),
    new RawCreatureArchetype("vrock", "FLYING", new String[] {"1 GOUGING PECK", "2 DISGUSTING CLAW"}, new String[] {"Winged Wrath", "Fiendish Fury", "Decay Deacon"}, -8169090448584422415L),
    new RawCreatureArchetype("jackal", "WALKING", new String[] {"1 GRABBING BITE"}, new String[] {"Desert Raider", "Bad Dog", "Wild Seeker"}, 1995687311993432579L),
    new RawCreatureArchetype("wolf", "WALKING", new String[] {"1 CRUSHING BITE", "1 STUNNING BURST"}, new String[] {"Pack Hunter", "Bad Dog", "Ice Shaman"}, 6885402032414600732L),
    new RawCreatureArchetype("hound", "WALKING", new String[] {"1 CRUSHING BITE"}, new String[] {"Pack Hunter", "Bad Dog", "Wild Seeker"}, 4885686759405061347L),
    new RawCreatureArchetype("paper golem", "WALKING", new String[] {"4 SLICING BLADE"}, new String[] {"Blade Disciple", "Fierce Frenzy", "Runic Warden"}, 2953396992116710822L),
    new RawCreatureArchetype("wood golem", "WALKING", new String[] {"2 CRUSHING SLAM", "2 SLICING SHARD"}, new String[] {"Forest Druid", "Hard Body", "Runic Warden"}, 3943147975391491136L),
    new RawCreatureArchetype("flesh golem", "WALKING", new String[] {"2 THRASHING SLAM"}, new String[] {"Blood Sucker", "Great Grappler", "Flowing Form"}, 849201145786630887L),
    new RawCreatureArchetype("clay golem", "WALKING", new String[] {"1 CRUSHING SLAM"}, new String[] {"Flowing Form", "Hard Body", "Runic Warden"}, 4391760566879421555L),
    new RawCreatureArchetype("stone golem", "WALKING", new String[] {"1 CRUSHING SLAM", "1 QUAKING BURST"}, new String[] {"Ground Pounder", "Hard Body", "Runic Warden"}, 7383067371407803201L),
    new RawCreatureArchetype("iron golem", "WALKING", new String[] {"1 CRUSHING SLAM", "1 SLICING BLADE"}, new String[] {"Blade Disciple", "Hard Body", "Runic Warden"}, -8936496063026370725L),
    new RawCreatureArchetype("crystal golem", "WALKING", new String[] {"2 SLICING BLADE", "4 SHINING RAY"}, new String[] {"Blade Disciple", "Glowing Guard", "Runic Warden"}, -8602762043943286929L),
    new RawCreatureArchetype("air elemental", "FLYING", new String[] {"3 SLICING SHARD"}, new String[] {"Fierce Frenzy", "Flowing Form", "Sky Soldier"}, -636380868768916959L),
    new RawCreatureArchetype("fire elemental", "WALKING", new String[] {"4 BURNING BURST"}, new String[] {"Fierce Frenzy", "Flowing Form", "Raging Inferno"}, 3225865827946745543L),
    new RawCreatureArchetype("earth elemental", "WALKING", new String[] {"2 QUAKING BURST"}, new String[] {"Ground Pounder", "Hard Body", "Mountain King"}, 1668622998676964136L),
    new RawCreatureArchetype("water elemental", "AMPHIBIOUS", new String[] {"1 SOAKING WAVE"}, new String[] {"Wave Knight", "Flowing Form", "Sea Skirmisher"}, 113760598825152778L),
    new RawCreatureArchetype("eye tyrant", "FLYING", new String[] {"4 ZAPPING RAY", "1 RIPPING BITE"}, new String[] {"Hypnotic Gaze", "Elite Arcanist", "Brain Bender"}, -918187043516489788L),
    new RawCreatureArchetype("giant", "WALKING", new String[] {"2 HUGE HAND"}, new String[] {"Barbaric Roar", "Great Grappler", "Mountain King"}, 6119088023253506841L),
    new RawCreatureArchetype("minotaur", "WALKING", new String[] {"2 LARGE HAND", "1 PIERCING HORN"}, new String[] {"Barbaric Roar", "Ground Pounder", "Wild Seeker"}, -1202262806553089337L),
    new RawCreatureArchetype("cyclops", "WALKING", new String[] {"2 HUGE HAND", "1 THRASHING SLAM"}, new String[] {"Hypnotic Gaze", "Great Grappler", "Deep Thinker"}, -385890650700984972L),
    new RawCreatureArchetype("militant orc", "WALKING", new String[] {"2 NORMAL HAND"}, new String[] {"Barbaric Roar", "Pack Hunter", "Desert Raider"}, -3214504342060268353L),
    new RawCreatureArchetype("orc shaman", "WALKING", new String[] {"2 NORMAL HAND", "1 CURSING BURST"}, new String[] {"Decay Deacon", "Pack Hunter", "Barbaric Roar"}, 7827793165701966348L),
    new RawCreatureArchetype("soldier", "WALKING", new String[] {"2 NORMAL HAND"}, new String[] {"Bold Crusader", "Pack Hunter", "Herd Protector"}, -4309525293555333065L),
    new RawCreatureArchetype("captain", "WALKING", new String[] {"2 NORMAL HAND"}, new String[] {"Mountain King", "Pack Hunter", "Herd Protector"}, 7674925972369098666L),
    new RawCreatureArchetype("elf", "WALKING", new String[] {"2 NORMAL HAND"}, new String[] {"Forest Druid", "Elite Arcanist", "Wild Seeker"}, -4631923711040537229L),
    new RawCreatureArchetype("troll", "WALKING", new String[] {"2 LARGE HAND", "1 DISGUSTING SLAM"}, new String[] {"Nasty Bandit", "Fierce Frenzy", "Great Grappler"}, 9051106556417559604L),
    new RawCreatureArchetype("gnome", "WALKING", new String[] {"2 SMALL HAND"}, new String[] {"Forest Druid", "Herd Protector", "Mischief Maker"}, -1681082230827917730L),
    new RawCreatureArchetype("leprechaun", "WALKING", new String[] {"2 SMALL HAND", "1 ZAPPING WAVE"}, new String[] {"Forest Druid", "Elite Arcanist", "Mischief Maker"}, -8557074310694152375L),
    new RawCreatureArchetype("dwarf", "WALKING", new String[] {"2 NORMAL HAND"}, new String[] {"Mountain King", "Herd Protector", "Hard Body"}, -2199292082315140563L),
    new RawCreatureArchetype("satyr", "WALKING", new String[] {"2 NORMAL HAND", "1 CRUSHING KICK"}, new String[] {"Ground Pounder", "Forest Druid", "Tough Fluff"}, 5510744552365601343L),
    new RawCreatureArchetype("forest centaur", "WALKING", new String[] {"2 NORMAL HAND", "2 CRUSHING KICK"}, new String[] {"Ground Pounder", "Herd Protector", "Wild Seeker"}, 6771218556805836170L),
    new RawCreatureArchetype("ogre", "WALKING", new String[] {"2 LARGE HAND", "2 THRASHING SLAM"}, new String[] {"Fierce Frenzy", "Great Grappler", "Barbaric Roar"}, -6602432760869142690L),
    new RawCreatureArchetype("quantum mechanic", "WALKING", new String[] {"2 NORMAL HAND", "1 ZAPPING RAY"}, new String[] {"Brain Bender", "Elite Arcanist", "Runic Warden"}, 4500011489872974216L),
    new RawCreatureArchetype("angel", "FLYING", new String[] {"2 NORMAL HAND", "1 SHINING BURST"}, new String[] {"Avian Overlord", "Glowing Guard", "Bold Crusader"}, -6480370744068956981L),
    new RawCreatureArchetype("tengu", "FLYING", new String[] {"2 NORMAL HAND", "1 GOUGING PECK"}, new String[] {"Winged Wrath", "Avian Overlord", "Mischief Maker"}, 6600680607457602903L),
    new RawCreatureArchetype("djinn", "FLYING", new String[] {"2 LARGE HAND", "2 SLICING SHARD"}, new String[] {"Sky Soldier", "Elite Arcanist", "Runic Warden"}, 1982386280331827633L),
    new RawCreatureArchetype("goblin", "WALKING", new String[] {"2 SMALL HAND", "1 DISGUSTING SLAM"}, new String[] {"Nasty Bandit", "Pack Hunter", "Jungle Stalker"}, 410308950140171355L),
    new RawCreatureArchetype("mind flayer", "AMPHIBIOUS", new String[] {"2 NORMAL HAND", "1 STUNNING WAVE"}, new String[] {"Moist Mangler", "Deep Thinker", "Brain Bender"}, -7861238756810728965L),
    new RawCreatureArchetype("bugbear", "WALKING", new String[] {"2 LARGE HAND"}, new String[] {"Nasty Bandit", "Pack Hunter", "Great Grappler"}, 1773738587823707370L),
    new RawCreatureArchetype("yeti", "WALKING", new String[] {"2 LARGE HAND", "1 FREEZING BURST"}, new String[] {"Mountain King", "Barbaric Roar", "Ice Shaman"}, 2583138928685694178L),
    new RawCreatureArchetype("medusa", "WALKING", new String[] {"2 NORMAL HAND", "1 MORPHING RAY"}, new String[] {"Shadow Sultan", "Deadly Venom", "Hypnotic Gaze"}, -9011393829961604230L),
    new RawCreatureArchetype("owlbear", "WALKING", new String[] {"2 THRASHING CLAW", "1 GOUGING PECK"}, new String[] {"Fierce Frenzy", "Great Grappler", "Forest Druid"}, -5399529800683515124L),
    new RawCreatureArchetype("kirin", "FLYING", new String[] {"2 SHINING KICK", "1 STUNNING HORN"}, new String[] {"Ground Pounder", "Glowing Guard", "Bold Crusader"}, -259112864433367443L),
    new RawCreatureArchetype("gargoyle", "FLYING", new String[] {"2 GOUGING CLAW", "2 CRUSHING KICK"}, new String[] {"Sky Soldier", "Fierce Frenzy", "Runic Warden"}, 257886560080615375L),
    new RawCreatureArchetype("kobold", "WALKING", new String[] {"2 SMALL HAND", "1 THRASHING BITE"}, new String[] {"Nasty Bandit", "Fierce Frenzy", "Mischief Maker"}, -3399523831488486766L),
    new RawCreatureArchetype("ape", "WALKING", new String[] {"2 CRUSHING SLAM", "1 RIPPING BITE"}, new String[] {"Jungle Stalker", "Herd Protector", "Fierce Frenzy"}, -4806512785026414578L),
    new RawCreatureArchetype("jabberwock", "WALKING", new String[] {"1 RIPPING BITE", "2 GOUGING CLAW"}, new String[] {"Fierce Frenzy", "Draconic Brute", "Brain Bender"}, 3809379008577137286L),
    new RawCreatureArchetype("umber hulk", "WALKING", new String[] {"2 CRUSHING CLAW", "1 STUNNING WAVE"}, new String[] {"Deep Thinker", "Bug Trooper", "Hypnotic Gaze"}, -3316313411859511602L),
    new RawCreatureArchetype("zruty", "WALKING", new String[] {"3 CRUSHING BITE"}, new String[] {"Fierce Frenzy", "Great Grappler", "Runic Warden"}, -2969200367346879570L),
    new RawCreatureArchetype("giant tick", "WALKING", new String[] {"1 GRABBING BITE"}, new String[] {"Blood Sucker", "Bug Trooper", "Great Grappler"}, -7267248702162945435L),
    new RawCreatureArchetype("giant beetle", "WALKING", new String[] {"1 SLICING BITE"}, new String[] {"Nasty Bandit", "Bug Trooper", "Hard Body"}, 2042767229121263081L),
    new RawCreatureArchetype("dung worm", "AMPHIBIOUS", new String[] {"1 DISGUSTING BITE"}, new String[] {"Nasty Bandit", "Moist Mangler", "Flowing Form"}, -2939678642163157744L),
    new RawCreatureArchetype("dragonfly", "FLYING", new String[] {"1 CRUSHING BITE"}, new String[] {"Sky Soldier", "Bug Trooper", "Winged Wrath"}, 5344158047393522551L),
    new RawCreatureArchetype("killer bee", "FLYING", new String[] {"1 PIERCING STING"}, new String[] {"Sky Soldier", "Bug Trooper", "Deadly Venom"}, -3632597699821464732L),
    new RawCreatureArchetype("giant spider", "WALKING", new String[] {"1 PIERCING BITE", "1 GRABBING WRAP"}, new String[] {"Deadly Venom", "Bug Trooper", "Great Grappler"}, -3480677509847048567L),
    new RawCreatureArchetype("scorpion", "WALKING", new String[] {"2 CRUSHING CLAW", "1 PIERCING STING"}, new String[] {"Deadly Venom", "Bug Trooper", "Desert Raider"}, -710247201995184534L),
    new RawCreatureArchetype("giant ant", "WALKING", new String[] {"1 RIPPING BITE"}, new String[] {"Great Grappler", "Bug Trooper", "Desert Raider"}, 1533796209908768355L),
    new RawCreatureArchetype("locust", "FLYING", new String[] {"1 SLICING BITE"}, new String[] {"Sky Soldier", "Bug Trooper", "Herd Protector"}, -6429508554095959435L),
    new RawCreatureArchetype("giant slug", "WALKING", new String[] {"1 DISGUSTING TONGUE"}, new String[] {"Decay Deacon", "Moist Mangler", "Great Grappler"}, 189396350955547859L),
    new RawCreatureArchetype("giant snail", "WALKING", new String[] {"1 DISGUSTING TONGUE"}, new String[] {"Decay Deacon", "Moist Mangler", "Runic Warden"}, -8189650310711932890L),
    new RawCreatureArchetype("feral hog", "WALKING", new String[] {"1 THRASHING BITE"}, new String[] {"Ground Pounder", "Forest Druid", "Plains Champ"}, -3788241246613831887L),
    new RawCreatureArchetype("bull", "WALKING", new String[] {"1 THRASHING HORN", "2 CRUSHING KICK"}, new String[] {"Ground Pounder", "Herd Protector", "Plains Champ"}, -7456326767372236269L),
    new RawCreatureArchetype("camel", "WALKING", new String[] {"1 CRUSHING BITE", "2 CRUSHING KICK"}, new String[] {"Ground Pounder", "Herd Protector", "Desert Raider"}, -3230505395977090562L),
    new RawCreatureArchetype("llama", "WALKING", new String[] {"1 CRUSHING BITE", "2 CRUSHING KICK"}, new String[] {"Ground Pounder", "Herd Protector", "Mountain King"}, 6762213091261200365L),
    new RawCreatureArchetype("gray goat", "WALKING", new String[] {"1 CRUSHING BITE", "1 PIERCING HORN"}, new String[] {"Herd Protector", "Mischief Maker", "Mountain King"}, 5434909564949671780L),
    new RawCreatureArchetype("white sheep", "WALKING", new String[] {"1 CRUSHING BITE", "2 CRUSHING KICK"}, new String[] {"Ground Pounder", "Herd Protector", "Tough Fluff"}, 2524541588782051610L),
    new RawCreatureArchetype("white unicorn", "WALKING", new String[] {"1 SHINING HORN", "2 CRUSHING KICK"}, new String[] {"Ground Pounder", "Glowing Guard", "Forest Druid"}, -8953168933558437139L),
    new RawCreatureArchetype("brown horse", "WALKING", new String[] {"1 THRASHING BITE", "2 CRUSHING KICK"}, new String[] {"Ground Pounder", "Plains Champ", "Herd Protector"}, 907176888610289789L),
    new RawCreatureArchetype("deer", "WALKING", new String[] {"2 THRASHING KICK", "1 RIPPING HORN"}, new String[] {"Ground Pounder", "Forest Druid", "Herd Protector"}, -3707529215686335419L),
    new RawCreatureArchetype("mastodon", "WALKING", new String[] {"2 CRUSHING KICK", "1 PIERCING BITE"}, new String[] {"Ground Pounder", "Tough Fluff", "Ice Shaman"}, -2339469342342171722L),
    new RawCreatureArchetype("wumpus", "WALKING", new String[] {"1 CRUSHING BITE", "2 STUNNING KICK"}, new String[] {"Primeval Lord", "Ground Pounder", "Mischief Maker"}, 6868410632393937294L),
    new RawCreatureArchetype("wyvern", "FLYING", new String[] {"1 RIPPING BITE", "2 GRABBING CLAW"}, new String[] {"Draconic Brute", "Sky Soldier", "Winged Wrath"}, 6309234934582069543L),
    new RawCreatureArchetype("dreadwyrm", "FLYING", new String[] {"1 CRUSHING BITE", "1 CURSING WAVE"}, new String[] {"Draconic Brute", "Master Dragon", "Decay Deacon"}, -7046628221298945963L),
    new RawCreatureArchetype("glendrake", "WALKING", new String[] {"1 RIPPING BITE", "1 SLICING WAVE"}, new String[] {"Master Dragon", "Jungle Stalker", "Forest Druid"}, 6424478953368075082L),
    new RawCreatureArchetype("firedrake", "WALKING", new String[] {"1 RIPPING BITE", "1 BURNING WAVE"}, new String[] {"Draconic Brute", "Master Dragon", "Raging Inferno"}, 9058788495063286806L),
    new RawCreatureArchetype("icewyrm", "FLYING", new String[] {"1 CRUSHING BITE", "1 FREEZING BURST"}, new String[] {"Draconic Brute", "Master Dragon", "Ice Shaman"}, 8639131985557757654L),
    new RawCreatureArchetype("sanddrake", "WALKING", new String[] {"1 RIPPING BITE", "1 QUAKING BURST"}, new String[] {"Draconic Brute", "Ground Pounder", "Desert Raider"}, -5648538065733602769L),
    new RawCreatureArchetype("storrmwyrm", "FLYING", new String[] {"1 CRUSHING BITE", "1 SHOCKING WAVE"}, new String[] {"Master Dragon", "Thunder Chief", "Sky Soldier"}, -3326914580224933003L),
    new RawCreatureArchetype("darkwyrm", "FLYING", new String[] {"1 CRUSHING BITE", "1 STUNNING WAVE"}, new String[] {"Master Dragon", "Shadow Sultan", "Winged Wrath"}, -270942571372655597L),
    new RawCreatureArchetype("lightwyrm", "FLYING", new String[] {"1 CRUSHING BITE", "1 SHINING BURST"}, new String[] {"Master Dragon", "Glowing Guard", "Bold Crusader"}, -5245836552444741352L),
    new RawCreatureArchetype("bogwyrm", "FLYING", new String[] {"1 CRUSHING BITE", "1 SOAKING BURST"}, new String[] {"Draconic Brute", "Deadly Venom", "Deep Thinker"}, 1298876448054455525L),
    new RawCreatureArchetype("sheenwyrm", "FLYING", new String[] {"1 CRUSHING BITE", "1 ZAPPING WAVE"}, new String[] {"Master Dragon", "Elite Arcanist", "Runic Warden"}, 1363036369182508413L),
    new RawCreatureArchetype("kingwyrm", "FLYING", new String[] {"1 CRUSHING BITE", "1 MORPHING WAVE"}, new String[] {"Master Dragon", "Barbaric Roar", "Mountain King"}, 4672541812393243171L),
    new RawCreatureArchetype("hydra", "AMPHIBIOUS", new String[] {"3 RIPPING BITE"}, new String[] {"Draconic Brute", "Fierce Frenzy", "Wave Knight"}, -2450063921507267685L),
    new RawCreatureArchetype("python", "AMPHIBIOUS", new String[] {"1 GRABBING BITE", "1 CRUSHING WRAP"}, new String[] {"Great Grappler", "Primeval Lord", "Flowing Form"}, 8167836842239373480L),
    new RawCreatureArchetype("cobra", "AMPHIBIOUS", new String[] {"1 PIERCING BITE"}, new String[] {"Deadly Venom", "Primeval Lord", "Hypnotic Gaze"}, 153523090058275481L),
    new RawCreatureArchetype("golden naga", "AMPHIBIOUS", new String[] {"1 CRUSHING WRAP", "1 ZAPPING BURST"}, new String[] {"Flowing Form", "Primeval Lord", "Hypnotic Gaze"}, 3300028373759682511L),
    new RawCreatureArchetype("cockatrice", "AMPHIBIOUS", new String[] {"1 GOUGING PECK", "1 MORPHING RAY"}, new String[] {"Brain Bender", "Avian Overlord", "Hypnotic Gaze"}, 5268698275233394389L),
    new RawCreatureArchetype("chameleon", "WALKING", new String[] {"1 GRABBING TONGUE", "1 CRUSHING BITE"}, new String[] {"Brain Bender", "Primeval Lord", "Great Grappler"}, 2482317835363988423L),
    new RawCreatureArchetype("crocodile", "AMPHIBIOUS", new String[] {"1 RIPPING BITE", "1 SOAKING SLAM"}, new String[] {"Wave Knight", "Primeval Lord", "Great Grappler"}, -308936665744901600L),
    new RawCreatureArchetype("red squirrel", "WALKING", new String[] {"1 CRUSHING BITE"}, new String[] {"Forest Druid", "Mischief Maker", "Wild Seeker"}, -3589991295574539349L),
    new RawCreatureArchetype("rabbit", "WALKING", new String[] {"1 CRUSHING BITE", "1 THRASHING KICK"}, new String[] {"Plains Champ", "Mischief Maker", "Herd Protector"}, 1088212608901775755L),
    new RawCreatureArchetype("giant rat", "WALKING", new String[] {"1 DISGUSTING BITE"}, new String[] {"Nasty Bandit", "Pack Hunter", "Mischief Maker"}, -723437535466682616L),
    new RawCreatureArchetype("rust monster", "WALKING", new String[] {"2 MORPHING TONGUE"}, new String[] {"Decay Deacon", "Bug Trooper", "Brain Bender"}, 2928941850657220106L),
    new RawCreatureArchetype("human zombie", "WALKING", new String[] {"1 DISGUSTING BITE", "1 THRASHING SLAM"}, new String[] {"Decay Deacon", "Great Grappler", "Blood Sucker"}, 5603418196011487017L),
    new RawCreatureArchetype("human mummy", "WALKING", new String[] {"2 CURSING SLAM", "1 CURSING WRAP"}, new String[] {"Decay Deacon", "Elite Arcanist", "Shadow Sultan"}, 6338151936013206157L),
    new RawCreatureArchetype("skeleton", "WALKING", new String[] {"2 NORMAL HAND", "1 CRUSHING BITE"}, new String[] {"Runic Warden", "Pack Hunter", "Hard Body"}, 404630328113949371L),
    new RawCreatureArchetype("lich", "WALKING", new String[] {"2 NORMAL HAND", "1 CURSING RAY"}, new String[] {"Runic Warden", "Elite Arcanist", "Shadow Sultan"}, 8095160074591940066L),
    new RawCreatureArchetype("vampire", "WALKING", new String[] {"2 NORMAL HAND", "1 CURSING BITE"}, new String[] {"Blood Sucker", "Shadow Sultan", "Hypnotic Gaze"}, 3642597949143099227L),
    new RawCreatureArchetype("spirit", "FLYING", new String[] {"1 CURSING BURST"}, new String[] {"Elite Arcanist", "Shadow Sultan", "Hypnotic Gaze"}, -5102334316440798769L),
    new RawCreatureArchetype("ghoul", "WALKING", new String[] {"2 DISGUSTING CLAW", "1 DISGUSTING BITE"}, new String[] {"Decay Deacon", "Pack Hunter", "Blood Sucker"}, -422049005647823122L),
  };

  public static final Map<String, RawCreatureArchetype> MAPPING = makeMap(
  "piranha", ENTRIES[0], "lobster", ENTRIES[1], "great white shark", ENTRIES[2], "blue whale",
  ENTRIES[3], "electric eel", ENTRIES[4], "kraken", ENTRIES[5], "sea tiger",
  ENTRIES[6], "platypus", ENTRIES[7], "frog", ENTRIES[8], "penguin",
  ENTRIES[9], "pelican", ENTRIES[10], "puffin", ENTRIES[11], "golden eagle",
  ENTRIES[12], "ostrich", ENTRIES[13], "leopard", ENTRIES[14], "lion",
  ENTRIES[15], "barn cat", ENTRIES[16], "bone devil", ENTRIES[17], "ice devil",
  ENTRIES[18], "vrock", ENTRIES[19], "jackal", ENTRIES[20], "wolf", ENTRIES[21],
  "hound", ENTRIES[22], "paper golem", ENTRIES[23], "wood golem", ENTRIES[24],
  "flesh golem", ENTRIES[25], "clay golem", ENTRIES[26], "stone golem",
  ENTRIES[27], "iron golem", ENTRIES[28], "crystal golem", ENTRIES[29],
  "air elemental", ENTRIES[30], "fire elemental", ENTRIES[31], "earth elemental",
  ENTRIES[32], "water elemental", ENTRIES[33], "eye tyrant", ENTRIES[34],
  "giant", ENTRIES[35], "minotaur", ENTRIES[36], "cyclops", ENTRIES[37],
  "militant orc", ENTRIES[38], "orc shaman", ENTRIES[39], "soldier",
  ENTRIES[40], "captain", ENTRIES[41], "elf", ENTRIES[42], "troll", ENTRIES[43],
  "gnome", ENTRIES[44], "leprechaun", ENTRIES[45], "dwarf", ENTRIES[46],
  "satyr", ENTRIES[47], "forest centaur", ENTRIES[48], "ogre", ENTRIES[49],
  "quantum mechanic", ENTRIES[50], "angel", ENTRIES[51], "tengu", ENTRIES[52],
  "djinn", ENTRIES[53], "goblin", ENTRIES[54], "mind flayer", ENTRIES[55],
  "bugbear", ENTRIES[56], "yeti", ENTRIES[57], "medusa", ENTRIES[58],
  "owlbear", ENTRIES[59], "kirin", ENTRIES[60], "gargoyle", ENTRIES[61],
  "kobold", ENTRIES[62], "ape", ENTRIES[63], "jabberwock", ENTRIES[64],
  "umber hulk", ENTRIES[65], "zruty", ENTRIES[66], "giant tick", ENTRIES[67],
  "giant beetle", ENTRIES[68], "dung worm", ENTRIES[69], "dragonfly",
  ENTRIES[70], "killer bee", ENTRIES[71], "giant spider", ENTRIES[72],
  "scorpion", ENTRIES[73], "giant ant", ENTRIES[74], "locust", ENTRIES[75],
  "giant slug", ENTRIES[76], "giant snail", ENTRIES[77], "feral hog",
  ENTRIES[78], "bull", ENTRIES[79], "camel", ENTRIES[80], "llama", ENTRIES[81],
  "gray goat", ENTRIES[82], "white sheep", ENTRIES[83], "white unicorn",
  ENTRIES[84], "brown horse", ENTRIES[85], "deer", ENTRIES[86], "mastodon",
  ENTRIES[87], "wumpus", ENTRIES[88], "wyvern", ENTRIES[89], "dreadwyrm",
  ENTRIES[90], "glendrake", ENTRIES[91], "firedrake", ENTRIES[92], "icewyrm",
  ENTRIES[93], "sanddrake", ENTRIES[94], "storrmwyrm", ENTRIES[95], "darkwyrm",
  ENTRIES[96], "lightwyrm", ENTRIES[97], "bogwyrm", ENTRIES[98], "sheenwyrm",
  ENTRIES[99], "kingwyrm", ENTRIES[100], "hydra", ENTRIES[101], "python",
  ENTRIES[102], "cobra", ENTRIES[103], "golden naga", ENTRIES[104], "cockatrice",
  ENTRIES[105], "chameleon", ENTRIES[106], "crocodile", ENTRIES[107],
  "red squirrel", ENTRIES[108], "rabbit", ENTRIES[109], "giant rat",
  ENTRIES[110], "rust monster", ENTRIES[111], "human zombie", ENTRIES[112],
  "human mummy", ENTRIES[113], "skeleton", ENTRIES[114], "lich", ENTRIES[115],
  "vampire", ENTRIES[116], "spirit", ENTRIES[117], "ghoul", ENTRIES[118]);

  public String name;

  public String move;

  public String[] attacks;

  public String[] jobs;

  private long __code;

  public RawCreatureArchetype() {
  }

  public RawCreatureArchetype(String name, String move, String[] attacks, String[] jobs,
      long __code) {
    this.name = name;
    this.move = move;
    this.attacks = attacks;
    this.jobs = jobs;
    this.__code = __code;
  }

  public long hash64() {
    return __code;
  }

  public int hashCode() {
    return (int)__code;
  }

  private static boolean stringArrayEquals(String[] left, String[] right) {
    if (left == right) return true;
    if (left == null || right == null) return false;
    final int len = left.length;
    if(len != right.length) return false;
    for (int i = 0; i < len; i++) { if(!java.util.Objects.equals(left[i], right[i])) return false; }
    return true;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RawCreatureArchetype other = (RawCreatureArchetype) o;
    if (name != null ? !name.equals(other.name) : other.name != null) return false;
    if (move != null ? !move.equals(other.move) : other.move != null) return false;
    if(!stringArrayEquals(attacks, other.attacks)) return false;
    if(!stringArrayEquals(jobs, other.jobs)) return false;
    return true;
  }

  public static RawCreatureArchetype get(String item) {
    return MAPPING.get(item);
  }
}
