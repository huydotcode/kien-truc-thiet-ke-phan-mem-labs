interface JsonWebService {
  processData(jsonData: object): void;
}

class AnalyticsService implements JsonWebService {
  processData(jsonData: object): void {
    console.log(`[AnalyticsService] Processing JSON data:`);
    console.dir(jsonData, { depth: null, colors: true });
  }
}

class LegacyXmlSystem {
  getXmlData(): string {
    return `
            <users>
                <user>
                    <id>1</id>
                    <name>John Doe</name>
                    <email>john@example.com</email>
                </user>
                <user>
                    <id>2</id>
                    <name>Jane Smith</name>
                    <email>jane@example.com</email>
                </user>
            </users>
        `;
  }
}

class XmlToJsonAdapter implements JsonWebService {
  constructor(private xmlSystem: LegacyXmlSystem) {}

  private convertXmlToJson(xml: string): object {
    console.log(`[Adapter] Converting XML to JSON...`);
    if (xml.includes("John Doe")) {
      return {
        users: [
          { id: 1, name: "John Doe", email: "john@example.com" },
          { id: 2, name: "Jane Smith", email: "jane@example.com" },
        ],
      };
    }
    return {};
  }

  processData(jsonData: object): void {}

  executeAdaptedProcess(service: JsonWebService): void {
    const xmlData = this.xmlSystem.getXmlData();
    console.log(`[Adapter] Fetched XML Data:\n${xmlData}`);

    const jsonData = this.convertXmlToJson(xmlData);
    service.processData(jsonData);
  }
}

// === Client Code ===
function testAdapter() {
  console.log("--- Testing XML to JSON Adapter (Adapter Pattern) ---");

  const xmlSystem = new LegacyXmlSystem();
  const jsonService = new AnalyticsService();

  const adapter = new XmlToJsonAdapter(xmlSystem);

  adapter.executeAdaptedProcess(jsonService);
}

testAdapter();
