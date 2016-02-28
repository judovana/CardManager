package cardmanager.impl;

import cardmanager.impl.networking.ServerPlayerCreator;
import cardmanager.networking.NetworkGame;
import cardmanager.gui.impl.GameViewOutput;
import cardmanager.impl.packages.Package;
import cardmanager.impl.packages.PackageDeffinition;
import cardmanager.gui.impl.MemoWatcher;

public class TempSettings {

    private NetworkLogger logger;
    private PackageDeffinition battlePackage;
    private NetworkGame network;
    private MemoWatcher memoWatcher;
    private Package pckg;
    private GameViewOutput gvo;
    private CollectionOperator collection;
    private ServerPlayerCreator scp;

    public CollectionOperator getCollection() {
        return collection;
    }

    public void setCollection(CollectionOperator collection) {
        this.collection = collection;
    }




    public void setGameViewOutput(GameViewOutput gvo) {
        this.gvo=gvo;
    }
    public GameViewOutput getGameViewOutput() {
        return gvo;
    }

    public void setMemoWatcher(MemoWatcher memoWatcher) {
        this.memoWatcher=memoWatcher;
    }

    public MemoWatcher getMemoWatcher() {
        return memoWatcher;
    }

    public void setPackage(Package unpackPackageDef) {
        pckg=unpackPackageDef;
    }

     public Package getPackage() {
        return pckg;
    }

    

    public void setLoger(NetworkLogger aThis) {
        this.logger = aThis;
    }

    public NetworkLogger getLogger() {
        return logger;
    }

    public PackageDeffinition getBattlePackage() {
        return battlePackage;
    }

    public void setBattlePackage(PackageDeffinition p) {
        this.battlePackage = p;
    }

    public void setNetwork(NetworkGame aThis) {
        this.network = aThis;
    }

    public NetworkGame getNetwork() {
        return network;
    }

    public void setServer(ServerPlayerCreator server) {
        this.scp=server;
    }

    public ServerPlayerCreator getServer() {
        return scp;
    }


}
