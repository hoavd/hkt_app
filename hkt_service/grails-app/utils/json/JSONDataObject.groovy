package json

import org.grails.web.json.JSONException
import org.grails.web.json.JSONObject
import org.grails.web.json.JSONTokener
import org.springframework.util.StringUtils

class JSONDataObject extends JSONObject implements Serializable {

    JSONDataObject() {
        super()
    }

    JSONDataObject(JSONObject jo, String[] sa) throws JSONException {
        super(jo, sa)
    }

    JSONDataObject(JSONTokener x) throws JSONException {
        super(x)
    }

    JSONDataObject(Map map) {
        super(map?:Collections.emptyMap())
    }

    JSONDataObject(String string) throws JSONException {
        super(StringUtils.isEmpty(string)?"{}":string)
    }

    private void writeObject(ObjectOutputStream output)
            throws IOException {
        output.writeUTF(this.toString())
    }

    private void readObject(ObjectInputStream input)
            throws IOException, ClassNotFoundException {
        JSONObject json = new JSONObject(input.readUTF())
        for (k in json.keySet()) {
            this.put(k, json.get(k))
        }
    }
}
