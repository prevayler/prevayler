The Skaringa XmlSnapshotManager has been removed from the main source path for 3 reasons:
 1) It was the only thing in Prevayler that required an external jar (skaringa-r1p8.jar or compatible).
 2) It is not free-software because it depends on Skaringa which is not free-software.
 3) It depends on package javax.xml.transform.stream which is available only with J2SE 1.4 (Prevayler runs on 1.3).

We have to think of a good way to distribute this alongside Prevayler.
Ideally, we should have a free-software XmlSnaphotManager that runs on J2SE 1.3.
